package com.ntunghoi.tutorials.oauth2.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class CustomizedErrorController implements ErrorController {
    @GetMapping("/error")
    public Map<@NonNull String, Object> showError(HttpServletRequest httpServletRequest) {
        Object statusCode = httpServletRequest.getAttribute("jakarta.servlet.error.status_code");
        Object exception = Optional.ofNullable(
                httpServletRequest.getAttribute("jakarta.servlet.error.exception")
        ).orElse("No exception found");
        Object path = httpServletRequest.getAttribute("jakarta.servlet.error.request_uri");

        return Map.of(
                "status", statusCode,
                "path", path,
                "exception", exception
        );
    }
}
