package com.ntunghoi.tutorials.oauth2.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @GetMapping("/csrf")
    public Map<String, String> getCsrf(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return Map.of(
                "token", csrfToken.getToken(),
                "HeaderName", csrfToken.getHeaderName(),
                "parameterName", csrfToken.getParameterName()
        );
    }
}
