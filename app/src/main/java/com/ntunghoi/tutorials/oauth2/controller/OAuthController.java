package com.ntunghoi.tutorials.oauth2.controller;

import com.ntunghoi.tutorials.oauth2.service.TokenExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class OAuthController {
    private final TokenExchangeService tokenExchangeService;

    public OAuthController(TokenExchangeService tokenExchangeService) {
        this.tokenExchangeService = tokenExchangeService;
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        log.info("Me");
        assert jwt.getExpiresAt() != null;
        return Map.of(
                "subject", jwt.getSubject(),
                "roles", jwt.getClaimAsStringList("roles"),
                "expires", jwt.getExpiresAt()
        );
    }

    @GetMapping("/exchange")
    public Map<String, String> exchange(@AuthenticationPrincipal Jwt jwt) {
        String newToken = tokenExchangeService.exchangeToken(
                jwt.getTokenValue(), "internal-server"
        );

        return Map.of("exchange_token", newToken);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Admin access granted";
    }
}
