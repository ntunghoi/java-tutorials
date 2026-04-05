package com.ntunghoi.tutorials.oauth2.controller.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class SimpleResponse {
    private String message;
}