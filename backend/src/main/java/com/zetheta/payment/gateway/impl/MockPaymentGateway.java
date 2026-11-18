
package com.zetheta.payment.gateway.impl;

import org.springframework.stereotype.Component;

import com.zetheta.payment.entity.PaymentTransaction;
import com.zetheta.payment.enums.PaymentStatus;
import com.zetheta.payment.gateway.PaymentGateway;
import com.zetheta.payment.gateway.GatewayPaymentResult;

import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public String getGatewayName() {
        return "MOCK_GATEWAY";
    }

    @Override
    public boolean supports(String paymentMethod) {
        return paymentMethod != null &&
                (paymentMethod.equalsIgnoreCase("UPI")
                || paymentMethod.equalsIgnoreCase("CARD"));
    }

    @Override
    public GatewayPaymentResult processPayment(
            PaymentTransaction transaction) {

        System.out.println("Processing payment through Mock Gateway");

        String gatewayTransactionId =
                "MOCK-" + UUID.randomUUID();

        return new GatewayPaymentResult(
                PaymentStatus.SUCCESS,
                gatewayTransactionId,
                "Mock payment processed successfully"
        );
    }
    @Override
public int getPriority() {
    return 1;
}

@Override
public boolean isAvailable() {
    return true;
}
}
