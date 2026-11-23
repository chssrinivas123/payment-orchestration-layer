
package com.zetheta.payment.gateway;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Component
public class GatewayRegistry {

    private final List<PaymentGateway> gateways;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public GatewayRegistry(
            List<PaymentGateway> gateways,
            CircuitBreakerRegistry circuitBreakerRegistry) {
        this.gateways = gateways;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    public List<PaymentGateway> getAvailableGateways(String paymentMethod) {

        return gateways.stream()
                .filter(gateway -> gateway.supports(paymentMethod))
                .filter(PaymentGateway::isAvailable)
                .filter(gateway -> {
                    String breakerName =
                            gateway.getGatewayName().equals("MOCK_GATEWAY")
                                    ? "mockGatewayA"
                                    : "mockGatewayB";

                    return circuitBreakerRegistry
                            .circuitBreaker(breakerName)
                            .getState()
                            != io.github.resilience4j.circuitbreaker
                                    .CircuitBreaker.State.OPEN;
                })
                .sorted(Comparator.comparingInt(
                        PaymentGateway::getPriority))
                .toList();
    }

    public PaymentGateway getGateway(String paymentMethod) {

        return getAvailableGateways(paymentMethod).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No available gateway supports: " + paymentMethod));
    }

    public List<PaymentGateway> getAllGateways() {
        return gateways;
    }
}
