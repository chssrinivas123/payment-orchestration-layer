
package com.zetheta.payment.gateway.impl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.stereotype.Component;
import com.zetheta.payment.config.GatewayProperties;

import com.zetheta.payment.entity.PaymentTransaction;
import com.zetheta.payment.enums.PaymentStatus;
import com.zetheta.payment.gateway.PaymentGateway;
import com.zetheta.payment.gateway.GatewayPaymentResult;

import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {
    private final GatewayProperties properties;
    public MockPaymentGateway(GatewayProperties properties) {
    this.properties = properties;
}
  
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

    @CircuitBreaker(name = "mockGatewayA")
    @Override
public GatewayPaymentResult processPayment(PaymentTransaction transaction) {

    System.out.println("Processing payment through Mock Gateway A");

    if (properties.getMockA().isSimulateFailure()) {
        System.out.println("Simulating Gateway A failure!");
        throw new RuntimeException("Simulated Gateway A failure");
    }

    String gatewayTransactionId = "MOCK-" + UUID.randomUUID();

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
    return properties.getMockA().isEnabled();
}
}

