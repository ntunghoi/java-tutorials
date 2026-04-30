package com.ntunghoi.tutorials.concurrency.service.payment;

import com.ntunghoi.tutorials.concurrency.exception.HighFraudRiskException;
import com.ntunghoi.tutorials.concurrency.model.AccountBalance;
import com.ntunghoi.tutorials.concurrency.model.FraudRiskLevel;
import com.ntunghoi.tutorials.concurrency.model.PaymentTransaction;
import com.ntunghoi.tutorials.concurrency.model.ShippingOrder;
import com.ntunghoi.tutorials.concurrency.service.AccountService;
import com.ntunghoi.tutorials.concurrency.service.FraudDetectionService;
import com.ntunghoi.tutorials.concurrency.service.ShippingService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.StructuredTaskScope;

@Slf4j
public class PaymentProcessingService {
    private final FraudDetectionService fraudDetectionService;
    private final AccountService accountService;
    private final ShippingService shippingService;

    public PaymentProcessingService(
            FraudDetectionService fraudDetectionService,
            AccountService accountService,
            ShippingService shippingService
    ) {
        this.fraudDetectionService = fraudDetectionService;
        this.accountService = accountService;
        this.shippingService = shippingService;
    }

    public void process(PaymentTransaction paymentTransaction) {
        CountDownLatch readyLatch = new CountDownLatch(1);
        // noinspection preview
        try (var scope = StructuredTaskScope.open()) {
            log.info("Process payment ({})", paymentTransaction.id());
            // noinspection preview
            var fraudRiskLevelSubtask = scope.fork(() -> {
                FraudRiskLevel fraudRiskLevel = fraudDetectionService.getRiskLevel(paymentTransaction);
                if (fraudRiskLevel == FraudRiskLevel.HIGH) {
                    throw new HighFraudRiskException(paymentTransaction);
                }

                log.info("Fraud risk level is okay. Trigger to prepare the shipping order");
                readyLatch.countDown();

                return fraudRiskLevel;
            });
            // noinspection preview
            var accountBalanceSubtask = scope.fork(() -> accountService.getBalance(paymentTransaction.accountId()));
            // noinspection preview
            var shippingOrderSubTask = scope.fork(() -> {
                log.info("Wait for signal to prepare shipping order");
                readyLatch.await();
                log.info("Proceed to prepare shipping order");
                return shippingService.getShippingOrder(paymentTransaction);
            });

            //noinspection preview
            scope.join();

            // noinspection preview
            FraudRiskLevel fraudRiskLevel = fraudRiskLevelSubtask.get();
            // noinspection preview
            AccountBalance accountBalance = accountBalanceSubtask.get();
            // noinspection preview
            ShippingOrder shippingOrder = shippingOrderSubTask.get();

            log.info(
                    "Fraud Risk Level: {} / Account Balance: {} - {} / Shipping Order: {}",
                    fraudRiskLevel.getDescription(),
                    accountBalance.accountId(),
                    accountBalance.balance(),
                    shippingOrder.paymentTransaction().id()
            );
        } catch (InterruptedException interruptedException) {
            log.error("Interrupted");
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
}
