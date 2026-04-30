package com.ntunghoi.tutorials.concurrency.model;

import lombok.Getter;

@Getter
public enum FraudRiskLevel {
    HIGH("High"), MEDIUM("Medium"), LOW("Low");

    private final String description;

    FraudRiskLevel(String description) {
        this.description = description;
    }
}
