package com.ntunghoi.tutorials.oauth2.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfiguration {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final List<UserDetails> IN_MEMORY_USERS = List.of(
            User.builder()
                    .username("alice")
                    .password(passwordEncoder.encode("password"))
                    .roles("USER", "ADMIN")
                    .build(),
            User.builder()
                    .username("bob")
                    .password(passwordEncoder.encode("password"))
                    .roles("USER")
                    .build()
    );

    // Chain 1: API endpoints
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(CsrfConfigurer::disable) // Disable CSRF for API endpoints
                .securityMatcher("/api/**")
                .securityMatcher(
                        new NegatedRequestMatcher(
                                PathPatternRequestMatcher.pathPattern("/api/csrf")
                        )
                )
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer -> authorizeHttpRequestsCustomizer
                        .requestMatchers(HttpMethod.POST, "/api/sessions").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/sessions").permitAll()
                        .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }

    // Chain 2: Default - login form, user facing pages
    @Bean
    @Order(2)
    public SecurityFilterChain defaultFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(csrfConfigurer -> csrfConfigurer
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler())
                )
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer -> authorizeHttpRequestsCustomizer
                        .requestMatchers("/login", "/logout", "/error", "/index.html").permitAll()
                        .requestMatchers("/api/csrf").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(IN_MEMORY_USERS);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(provider);
    }
}
