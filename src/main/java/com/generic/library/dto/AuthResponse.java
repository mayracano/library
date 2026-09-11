package com.generic.library.dto;

public record AuthResponse(
        String token,
        String tokenType
) {}
