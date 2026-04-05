package com.ntunghoi.tutorials.oauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {
    @GetMapping("/error")
    public String showError() {
        return "Error";
    }
}
