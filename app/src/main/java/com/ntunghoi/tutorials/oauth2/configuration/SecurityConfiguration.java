package com.ntunghoi.tutorials.oauth2.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
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
                    .username("peter")
                    .password(passwordEncoder.encode("kCmPtc59pjLw"))
                    .roles("USER", "ADMIN")
                    .build(),
            User.builder()
                    .username("bob")
                    .password(passwordEncoder.encode("kCmPtc59pjLw"))
                    .roles("USER")
                    .build()
    );

    // Chain 1: Authorization Server /oauth2/* endpoints  (highest priority)
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity httpSecurity) {
        OAuth2AuthorizationServerConfigurer auth2AuthorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();

        httpSecurity
                .securityMatcher(auth2AuthorizationServerConfigurer.getEndpointsMatcher())
                .with(
                        auth2AuthorizationServerConfigurer,
                        authorizationServer ->
                                authorizationServer.oidc(Customizer.withDefaults()))
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer ->
                        authorizeHttpRequestsCustomizer
                                .requestMatchers("/index.html").permitAll()
                                .anyRequest().authenticated()
                )
                // Redirect to /login if unauthenticated on auth server endpoints
                .exceptionHandling(exceptionHandlingCustomize -> {
                    exceptionHandlingCustomize
                            .defaultAuthenticationEntryPointFor(
                                    new LoginUrlAuthenticationEntryPoint("/login"),
                                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                            );
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return httpSecurity.build();
    }

    // Chain 2: API endpoints
    @Bean
    @Order(2)
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

    // Chain 3: Default - login form, user facing pages
    @Bean
    @Order(3)
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
