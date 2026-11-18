
package com.zetheta.payment.gateway;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class GatewayRegistry {

    private final List<PaymentGateway> gateways;

    public GatewayRegistry(List<PaymentGateway> gateways) {
        this.gateways = gateways;
    }

    public PaymentGateway getGateway(String paymentMethod) {

        return gateways.stream()
                .filter(gateway -> gateway.supports(paymentMethod))
                .filter(PaymentGateway::isAvailable)
                .min(Comparator.comparingInt(
                        PaymentGateway::getPriority))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No available gateway supports: "
                                        + paymentMethod
                        )
                );
    }

    public List<PaymentGateway> getAllGateways() {
        return gateways;
    }
}
