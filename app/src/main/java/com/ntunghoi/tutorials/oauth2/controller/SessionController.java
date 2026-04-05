package com.ntunghoi.tutorials.oauth2.controller;

import com.ntunghoi.tutorials.oauth2.controller.dto.SimpleResponse;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessions")
@Slf4j
public class SessionController {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public SessionController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = new HttpSessionSecurityContextRepository();
    }

    @PostMapping
    public ResponseEntity<@NonNull SimpleResponse> login(
            LoginRequest loginRequest,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            );
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            securityContextRepository.saveContext(securityContext, httpServletRequest, httpServletResponse);

            return Optional.ofNullable((UserDetails) authentication.getPrincipal())
                    .map(userDetails -> ResponseEntity.ok(
                            (SimpleResponse) UserDetailsResponse.builder()
                                    .username(userDetails.getUsername())
                                    .roles(
                                            userDetails.getAuthorities().stream()
                                                    .map(GrantedAuthority::getAuthority)
                                                    .toList())
                                    .message("Logged in successfully")
                                    .build()
                    ))
                    .orElse(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(
                                            SimpleResponse.builder()
                                                    .message("User details not found for username " + loginRequest.username)
                                                    .build()
                                    )
                    );
        } catch (BadCredentialsException badCredentialsException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            SimpleResponse.builder()
                                    .message("Invalid username / password")
                                    .build()
                    );
        }
    }

    @GetMapping()
    public ResponseEntity<@NonNull SimpleResponse> getSession(@AuthenticationPrincipal UserDetails user) {
        log.info("Username: {}", user.getUsername());
        return Optional.ofNullable(getUserDetails())
                .map(userDetails ->
                        ResponseEntity.ok(
                                (SimpleResponse) UserDetailsResponse.builder()
                                        .username(userDetails.getUsername())
                                        .roles(userDetails.getAuthorities().stream()
                                                .map(GrantedAuthority::getAuthority)
                                                .toList()
                                        )
                                        .message("Done")
                                        .build()
                        )
                )
                .orElse(
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(
                                        SimpleResponse.builder()
                                                .message("Not authenticated")
                                                .build()
                                )
                );
    }

    private @Nullable UserDetails getUserDetails() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(authentication -> (UserDetails) authentication.getPrincipal())
                .orElse(null);
    }

    @DeleteMapping()
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SecurityContextHolder.clearContext();
        Optional.ofNullable(httpServletRequest.getSession(false))
                .ifPresent(HttpSession::invalidate);
        return ResponseEntity.ok(Map.of(
                "message", "Logged out"
        ));
    }

    public record LoginRequest(String username, String password) {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    public static class UserDetailsResponse extends SimpleResponse {
        private String username;
        private List<String> roles;
    }
}
