package com.ntunghoi.tutorials.oauth2.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @GetMapping("/demo")
    public String showDemo(@AuthenticationPrincipal UserDetails user) {
        return "Hello, " + user.getUsername() + "!";
    }

    @GetMapping("/error")
    public String showError() {
        return "Error";
    }
}
