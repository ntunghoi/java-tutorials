package com.ntunghoi.tutorials.oauth2.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class TokenExchangeService {
    private static final String TOKEN_URL = "http://localhost:8080/oauth2/token";

    private final RestClient restClient = RestClient.create();

    public String exchangeToken(String subjectToken, String targetAudience) {
        TokenResponse tokenResponse = restClient.post()
                .uri(TOKEN_URL)
                .headers(headers -> headers.setBasicAuth("my-client", "secret"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(buildParams(subjectToken, targetAudience))
                .retrieve()
                .body(TokenResponse.class);

        return tokenResponse != null ? tokenResponse.accessToken() : null;
    }

    private MultiValueMap<@NonNull String, String> buildParams(String token, String audience) {
        MultiValueMap<@NonNull String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "urn:ietf:params:oauth:grant-type:token_exchange");
        params.add("subject_name", token);
        params.add("subject_token_type", "urn:ietf:params:oauth:token-type:access_token");
        params.add("requested_token_type", "urn:ietf:params:oauth:token-type:access_token");
        params.add("audience", audience);

        return params;
    }

    record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("expires_in") long expiresIn
    ) {
    }
}
