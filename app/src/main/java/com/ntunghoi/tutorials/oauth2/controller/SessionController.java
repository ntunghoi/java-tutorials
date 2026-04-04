package com.ntunghoi.tutorials.oauth2.controller;

import com.ntunghoi.tutorials.oauth2.dto.UserSessionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    @PostMapping
    public UserSessionDto login() {
        return new UserSessionDto("userId");
    }
}
