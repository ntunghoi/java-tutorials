package com.ntunghoi.tutorials.concurrency.model;

import java.math.BigDecimal;

public record AccountBalance(String accountId, BigDecimal balance) {
}
