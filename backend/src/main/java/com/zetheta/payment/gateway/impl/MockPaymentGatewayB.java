
package com.zetheta.payment.gateway.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.zetheta.payment.entity.PaymentTransaction;
import com.zetheta.payment.enums.PaymentStatus;
import com.zetheta.payment.gateway.GatewayPaymentResult;
import com.zetheta.payment.gateway.PaymentGateway;

@Component
public class MockPaymentGatewayB implements PaymentGateway {

    @Override
    public String getGatewayName() {
        return "MOCK_GATEWAY_B";
    }

    @Override
    public boolean supports(String paymentMethod) {
        return paymentMethod != null
                && paymentMethod.equalsIgnoreCase("NETBANKING");
    }

    @Override
    public GatewayPaymentResult processPayment(
            PaymentTransaction transaction) {

        System.out.println(
                "Processing payment through Mock Gateway B"
        );

        String gatewayTransactionId =
                "MOCK-B-" + UUID.randomUUID();

        return new GatewayPaymentResult(
                PaymentStatus.SUCCESS,
                gatewayTransactionId,
                "Payment successfully processed by Mock Gateway B"
        );
    }
}
