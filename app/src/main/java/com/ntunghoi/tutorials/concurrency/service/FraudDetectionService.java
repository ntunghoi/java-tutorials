package com.ntunghoi.tutorials.concurrency.service;

import com.ntunghoi.tutorials.concurrency.model.FraudRiskLevel;
import com.ntunghoi.tutorials.concurrency.model.PaymentTransaction;
import com.ntunghoi.tutorials.concurrency.configuration.AppConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class FraudDetectionService {
    private static final List<FraudRiskLevel> fraudRiskLevels = Arrays.stream(FraudRiskLevel.values()).toList();

    public FraudRiskLevel getRiskLevel(PaymentTransaction paymentTransaction) throws InterruptedException {
        log.info("Get risk level");

        try {
            long ms = ThreadLocalRandom.current().nextLong(AppConfiguration.MIM_SLEEP_TIME, AppConfiguration.MAX_SLEEP_TIME);
            log.info("Sleep for {}ms for payment ({}) before returning fraud risk level", ms, paymentTransaction.id());
            Thread.sleep(ms);
        } catch (InterruptedException interruptedException) {
            log.info("Fraud risk level detection was interrupted");
            throw interruptedException;
        }

        int fraudRiskLevelIndex = ThreadLocalRandom.current().nextInt(0, fraudRiskLevels.size());
        FraudRiskLevel fraudRiskLevel = fraudRiskLevels.get(fraudRiskLevelIndex);
        log.info("Return fraud risk level ({}) for payment ({})", fraudRiskLevel.getDescription(), paymentTransaction.id());
        return fraudRiskLevel;
    }
}
