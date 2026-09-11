package com.generic.library.service;

import com.generic.library.config.JwtService;
import com.generic.library.dto.AuthResponse;
import com.generic.library.dto.LoginRequest;
import com.generic.library.model.Role;
import com.generic.library.model.User;
import com.generic.library.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthService authService;

    @Test
    void shouldLoginSuccessfullyAndReturnToken() {
        LoginRequest request = new LoginRequest("admin@library.com", "password123");

        User mockUser = new User();
        mockUser.setEmail("admin@library.com");
        mockUser.setRole(Role.LIBRARIAN);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(anyMap(), eq(request.email()))).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mocked-jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldReturnUserNotFound() {
        LoginRequest request = new LoginRequest("notFound@library.com", "password123");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
