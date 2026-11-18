
package com.zetheta.payment.gateway;

import com.zetheta.payment.entity.PaymentTransaction;

public interface PaymentGateway {

    String getGatewayName();

    boolean supports(String paymentMethod);

    GatewayPaymentResult processPayment(
            PaymentTransaction transaction
    );

    // Lower number means higher priority
    int getPriority();

    // Checks whether the gateway is available
    boolean isAvailable();
}
