package com.ntunghoi.tutorials.concurrency;

import com.ntunghoi.tutorials.concurrency.model.PaymentTransaction;
import com.ntunghoi.tutorials.concurrency.service.AccountService;
import com.ntunghoi.tutorials.concurrency.service.FraudDetectionService;
import com.ntunghoi.tutorials.concurrency.service.ShippingService;
import com.ntunghoi.tutorials.concurrency.service.payment.PaymentProcessingService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JavaConcurrencyApp {
    private final PaymentProcessingService paymentProcessingService;

    static void main() {
        PaymentTransaction paymentTransaction = new PaymentTransaction(
                "ba751eaa-c8c2-4e09-87aa-b25faa95f092",
                "f59d2f81-7afa-4a47-9f09-cefbff4f2d38"
        );

        FraudDetectionService fraudDetectionService = new FraudDetectionService();
        AccountService accountService = new AccountService();
        ShippingService shippingService = new ShippingService();

        JavaConcurrencyApp javaConcurrencyApp = new JavaConcurrencyApp(
                fraudDetectionService,
                accountService,
                shippingService
        );
        try {
            javaConcurrencyApp.process(paymentTransaction);
        } catch (InterruptedException interruptedException) {
            log.error("Payment processing was interrupted");
        }
    }

    public JavaConcurrencyApp(
            FraudDetectionService fraudDetectionService,
            AccountService accountService,
            ShippingService shippingService
    ) {
        this.paymentProcessingService = new PaymentProcessingService(
                fraudDetectionService,
                accountService,
                shippingService
        );
    }

    public void process(PaymentTransaction paymentTransaction)
            throws InterruptedException {
        paymentProcessingService.process(paymentTransaction);
    }
}
