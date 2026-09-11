package com.generic.library.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "TXV5SW1wb3J0YW50ZUNsYXZlU2VjcmV0YVBhcmFTcHJpbmdCb290TmV2ZWwyU2VtaVNlbmlvcg==",
                86400000L
        );
        jwtService = new JwtService(properties);
    }

    @Test
    void shouldGenerateAndExtractUsernameSuccessfully() {
        String email = "test@library.com";

        String token = jwtService.generateToken(email);
        String extractedEmail = jwtService.extractUsername(token);

        assertThat(token).isNotEmpty();
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void shouldValidateCorrectToken() {
        String email = "test@library.com";
        String token = jwtService.generateToken(email);

        boolean isValid = jwtService.isTokenValid(token, email);

        assertThat(isValid).isTrue();
    }

    @Test
    void shouldExtractCustomRoleClaim() {
        String email = "admin@library.com";
        var claims = new HashMap<String, Object>();
        claims.put("role", "LIBRARIAN");

        String token = jwtService.generateToken(claims, email);
        String role = jwtService.extractRole(token);

        assertThat(role).isEqualTo("LIBRARIAN");
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotMatchToken() {
        String tokenEmail = "real-owner@library.com";
        String wrongEmail = "impostor@library.com";

        String token = jwtService.generateToken(tokenEmail);
        boolean isValid = jwtService.isTokenValid(token, wrongEmail);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotMatchAndTokenIsValid() {
        String realOwnerEmail = "real-owner@library.com";
        String token = jwtService.generateToken(realOwnerEmail);

        String distinctUserEmail = "different-user@library.com";
        boolean isValid = jwtService.isTokenValid(token, distinctUserEmail);

        assertThat(isValid).isFalse();
    }


}
