package com.zetheta.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetheta.payment.entity.PaymentTransaction;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByTransactionId(String transactionId);

    Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);
}
