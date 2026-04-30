package com.ntunghoi.tutorials.concurrency.service;

import com.ntunghoi.tutorials.concurrency.model.AccountBalance;
import com.ntunghoi.tutorials.concurrency.configuration.AppConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class AccountService {
    public AccountBalance getBalance(String accountId)
            throws InterruptedException {
        log.info("Get balance for account with ID {}", accountId);

        try {
            long ms = ThreadLocalRandom.current().nextLong(AppConfiguration.MIM_SLEEP_TIME, AppConfiguration.MAX_SLEEP_TIME);
            log.info("Sleep for {}ms before returning the account balance", ms);
            Thread.sleep(ms);
        } catch (InterruptedException interruptedException) {
            log.error("Retrieving balance for account ({}) was interrupted", accountId);
            throw interruptedException;
        }

        log.info("Return balance for account {} now", accountId);
        return new AccountBalance(accountId, BigDecimal.ZERO);
    }
}
