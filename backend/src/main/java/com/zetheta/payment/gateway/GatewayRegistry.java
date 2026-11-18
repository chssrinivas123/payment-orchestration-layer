
package com.zetheta.payment.gateway;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class GatewayRegistry {

    private final List<PaymentGateway> gateways;

    public GatewayRegistry(List<PaymentGateway> gateways) {
        this.gateways = gateways;
    }

    // Find a gateway that supports the payment method
    public PaymentGateway getGateway(String paymentMethod) {

        return gateways.stream()
                .filter(gateway -> gateway.supports(paymentMethod))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No payment gateway supports: "
                                        + paymentMethod
                        )
                );
    }

    // Return all registered gateways
    public List<PaymentGateway> getAllGateways() {
        return gateways;
    }
}
