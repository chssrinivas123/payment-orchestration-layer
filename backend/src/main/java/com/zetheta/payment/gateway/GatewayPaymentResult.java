package com.zetheta.payment.gateway;

import com.zetheta.payment.enums.PaymentStatus;

public record GatewayPaymentResult(
        PaymentStatus status,
        String gatewayTransactionId,
        String message
) {
}
