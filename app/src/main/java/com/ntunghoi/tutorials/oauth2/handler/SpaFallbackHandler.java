package com.ntunghoi.tutorials.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class SpaFallbackHandler {
    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNotFoundException(HttpServletRequest httpServletRequest) {
        String requestUri = httpServletRequest.getRequestURI();
        log.info("No resource found for uri {}", requestUri);

        // 404 for API endpoint requests
        if (requestUri.startsWith("/api/")) {
            return null;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("forward:/index.html");

        return modelAndView;
    }
}
