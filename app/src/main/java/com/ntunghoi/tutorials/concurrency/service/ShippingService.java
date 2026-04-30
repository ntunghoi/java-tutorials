package com.ntunghoi.tutorials.concurrency.service;

import com.ntunghoi.tutorials.concurrency.model.PaymentTransaction;
import com.ntunghoi.tutorials.concurrency.configuration.AppConfiguration;
import com.ntunghoi.tutorials.concurrency.model.ShippingOrder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class ShippingService {
    public ShippingOrder getShippingOrder(PaymentTransaction paymentTransaction)
            throws InterruptedException {
        try {
            long ms = ThreadLocalRandom.current().nextLong(AppConfiguration.MIM_SLEEP_TIME, AppConfiguration.MAX_SLEEP_TIME);
            log.info("Sleep for {}ms before generating a shipping order for payment ({})", ms, paymentTransaction.id());
            Thread.sleep(ms);
        } catch (InterruptedException interruptedException) {
            log.error("Audit log for payment ({}) was interrupted", paymentTransaction.id());
            throw interruptedException;
        }

        ShippingOrder shippingOrder = new ShippingOrder("452b42af-6093-4116-af2a-314e50252a70", paymentTransaction);
        log.info("Generate a shipping order for payment ({}): {}", paymentTransaction.id(), shippingOrder.id());
        return shippingOrder;
    }
}
