
package com.zetheta.payment.gateway.impl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.zetheta.payment.config.GatewayProperties;
import com.zetheta.payment.entity.PaymentTransaction;
import com.zetheta.payment.enums.PaymentStatus;
import com.zetheta.payment.gateway.GatewayPaymentResult;
import com.zetheta.payment.gateway.PaymentGateway;

@Component
public class MockPaymentGatewayB implements PaymentGateway {

    private final GatewayProperties properties;

    public MockPaymentGatewayB(GatewayProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getGatewayName() {
        return "MOCK_GATEWAY_B";
    }

    @Override
    public boolean supports(String paymentMethod) {
        return paymentMethod != null &&
                (paymentMethod.equalsIgnoreCase("UPI")
                || paymentMethod.equalsIgnoreCase("NETBANKING"));
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean isAvailable() {
        return properties.getMockB().isEnabled();
    }
    @CircuitBreaker(name = "mockGatewayB")
    @Override
    public GatewayPaymentResult processPayment(
            PaymentTransaction transaction) {

        System.out.println(
                "Processing payment through Mock Gateway B");

        String gatewayTransactionId =
                "MOCK-B-" + UUID.randomUUID();

        return new GatewayPaymentResult(
                PaymentStatus.SUCCESS,
                gatewayTransactionId,
                "Payment successfully processed by Mock Gateway B"
        );
    }
}
