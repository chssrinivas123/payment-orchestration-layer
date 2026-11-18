package com.zetheta.payment.gateway;

import com.zetheta.payment.entity.PaymentTransaction;

public interface PaymentGateway {

    // Returns the name of the payment gateway
    String getGatewayName();

    // Checks whether this gateway supports a payment method
    boolean supports(String paymentMethod);

    // Processes a payment through this gateway
    GatewayPaymentResult processPayment(
            PaymentTransaction transaction
    );
}
