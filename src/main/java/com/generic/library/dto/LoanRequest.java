package com.generic.library.dto;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "User ID cannot be null")
        Long userId,
        @NotNull(message = "Book ID cannot be null")
        Long bookId) {
}
