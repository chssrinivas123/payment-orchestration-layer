package com.zetheta.payment.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import com.zetheta.payment.entity.PaymentTransaction;
import com.zetheta.payment.enums.PaymentStatus;
import com.zetheta.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final String IDEMPOTENCY_KEY = "ORDER-5001-KEY";

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void repeatedIdenticalIdempotencyKeyReturnsExistingTransaction() {
        PaymentTransaction existing = existingPayment("UPI");
        when(paymentRepository.findByIdempotencyKey(IDEMPOTENCY_KEY))
                .thenReturn(Optional.of(existing));

        PaymentTransaction result = paymentService.createPayment(
                "ORD-5001", new BigDecimal("1000.00"), "INR", "UPI", IDEMPOTENCY_KEY);

        assertSame(existing, result);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void idempotencyKeyReusedForDifferentPaymentMethodIsRejected() {
        when(paymentRepository.findByIdempotencyKey(IDEMPOTENCY_KEY))
                .thenReturn(Optional.of(existingPayment("UPI")));

        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(
                "ORD-5001", new BigDecimal("1000.00"), "INR", "CARD", IDEMPOTENCY_KEY));

        verify(paymentRepository, never()).save(any());
    }

    private PaymentTransaction existingPayment(String paymentMethod) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrderId("ORD-5001");
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setCurrency("INR");
        transaction.setPaymentMethod(paymentMethod);
        transaction.setIdempotencyKey(IDEMPOTENCY_KEY);
        transaction.setStatus(PaymentStatus.CREATED);
        return transaction;
    }
}
