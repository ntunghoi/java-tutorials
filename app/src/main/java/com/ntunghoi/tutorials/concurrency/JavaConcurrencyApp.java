package com.ntunghoi.tutorials.concurrency;

import com.ntunghoi.tutorials.concurrency.model.PaymentTransaction;
import com.ntunghoi.tutorials.concurrency.service.AccountService;
import com.ntunghoi.tutorials.concurrency.service.FraudDetectionService;
import com.ntunghoi.tutorials.concurrency.service.ShippingService;
import com.ntunghoi.tutorials.concurrency.service.WordFrequencyCountService;
import com.ntunghoi.tutorials.concurrency.service.payment.PaymentProcessingService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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
            //javaConcurrencyApp.process(paymentTransaction);
            javaConcurrencyApp.countWords();
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

    public void countWords() throws InterruptedException {
        WordFrequencyCountService wordFrequencyCountService = new WordFrequencyCountService();
        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            String[] words = {
                    "java", "concurrency", "java", "thread",
                    "java", "longadder", "concurrency", "java"
            };

            for (int index = 0; index < words.length; index++) {
                String word = words[index % words.length];
                pool.submit(() -> wordFrequencyCountService.record(word));
            }

            pool.shutdown();
            boolean isTerminated = pool.awaitTermination(30, TimeUnit.SECONDS);
            if (!isTerminated) {
                log.error("Timed out in terminating the thread pool");
            }
        }

        wordFrequencyCountService.snapshot().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> log.info("{} {}", entry.getKey(), entry.getValue()));
    }
}
