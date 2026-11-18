
package com.zetheta.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.gateway")
public class GatewayProperties {

    private MockGateway mockA = new MockGateway();
    private MockGateway mockB = new MockGateway();

    public MockGateway getMockA() {
        return mockA;
    }

    public void setMockA(MockGateway mockA) {
        this.mockA = mockA;
    }

    public MockGateway getMockB() {
        return mockB;
    }

    public void setMockB(MockGateway mockB) {
        this.mockB = mockB;
    }

    public static class MockGateway {

        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
