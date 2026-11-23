
package com.zetheta.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetheta.payment.entity.PaymentTransaction;
import com.zetheta.payment.enums.PaymentStatus;
import com.zetheta.payment.gateway.GatewayPaymentResult;
import com.zetheta.payment.gateway.GatewayRegistry;
import com.zetheta.payment.gateway.PaymentGateway;
import com.zetheta.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final GatewayRegistry gatewayRegistry;

    public PaymentService(
            PaymentRepository paymentRepository,
            GatewayRegistry gatewayRegistry) {
        this.paymentRepository = paymentRepository;
        this.gatewayRegistry = gatewayRegistry;
    }

    @Transactional
    public PaymentTransaction createPayment(
            String orderId,
            BigDecimal amount,
            String currency,
            String paymentMethod,
            String idempotencyKey) {

        // 1. Validate payment details
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero");
        }

        if (currency == null || !currency.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Invalid currency");
        }

        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException(
                    "Payment method is required");
        }

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Idempotency key is required");
        }

        String normalizedCurrency = currency.toUpperCase();

        // 2. Check idempotency
        var existingPayment =
                paymentRepository.findByIdempotencyKey(idempotencyKey);

        if (existingPayment.isPresent()) {
            PaymentTransaction existing = existingPayment.get();

            boolean sameRequest =
                    existing.getOrderId().equals(orderId)
                    && existing.getAmount().compareTo(amount) == 0
                    && existing.getCurrency().equals(normalizedCurrency)
                    && existing.getPaymentMethod()
                            .equalsIgnoreCase(paymentMethod);

            if (!sameRequest) {
                throw new IllegalArgumentException(
                        "Idempotency key already used with different payment details");
            }

            return existing;
        }

        // 3. Create payment transaction
        PaymentTransaction transaction = new PaymentTransaction();

        transaction.setTransactionId("TXN-" + UUID.randomUUID());
        transaction.setOrderId(orderId);
        transaction.setAmount(amount);
        transaction.setCurrency(normalizedCurrency);
        transaction.setPaymentMethod(paymentMethod.toUpperCase());
        transaction.setIdempotencyKey(idempotencyKey);
        transaction.setStatus(PaymentStatus.CREATED);

        // 4. Save initial transaction
        paymentRepository.save(transaction);

        // 5. Get eligible gateways in priority order
        List<PaymentGateway> availableGateways =
                gatewayRegistry.getAvailableGateways(paymentMethod);

        if (availableGateways.isEmpty()) {
            transaction.setStatus(PaymentStatus.FAILED);
            return paymentRepository.save(transaction);
        }

        // 6. Attempt payment with gateway failover
        for (PaymentGateway gateway : availableGateways) {

            transaction.setGatewayName(gateway.getGatewayName());
            transaction.setStatus(PaymentStatus.PROCESSING);

            try {
                System.out.println(
                        "Trying payment gateway: "
                                + gateway.getGatewayName());

                GatewayPaymentResult result =
                        gateway.processPayment(transaction);

                transaction.setStatus(result.status());

                System.out.println(
                        "Payment result: " + result.status());

                return paymentRepository.save(transaction);

            } catch (RuntimeException exception) {

                System.err.println(
                        "Gateway failed: "
                                + gateway.getGatewayName()
                                + " - "
                                + exception.getMessage());
            }
        }

        // 7. All gateways failed
        transaction.setStatus(PaymentStatus.FAILED);

        return paymentRepository.save(transaction);
    }
}
