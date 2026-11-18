
package com.zetheta.payment.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zetheta.payment.entity.PaymentTransaction;
import com.zetheta.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentTransaction> createPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PaymentRequest request) {

        PaymentTransaction transaction = paymentService.createPayment(
                request.getOrderId(),
                request.getAmount(),
                request.getCurrency(),
                request.getPaymentMethod(),
                idempotencyKey
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }

    public static class PaymentRequest {

        private String orderId;
        private BigDecimal amount;
        private String currency;
        private String paymentMethod;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }
}
