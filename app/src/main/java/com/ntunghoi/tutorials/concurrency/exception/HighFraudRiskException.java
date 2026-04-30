package com.ntunghoi.tutorials.concurrency.exception;

import com.ntunghoi.tutorials.concurrency.model.PaymentTransaction;
import lombok.Getter;

@Getter
public class HighFraudRiskException extends Exception {
    private final String paymentId;

    public HighFraudRiskException(String paymentId) {
        this.paymentId = paymentId;
        super("High fraud risk for payment with ID " + paymentId);
    }

    public HighFraudRiskException(PaymentTransaction paymentTransaction) {
        this(paymentTransaction.id());
    }
}
