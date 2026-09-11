package com.generic.library.service;

import com.generic.library.config.JwtService;
import com.generic.library.dto.AuthResponse;
import com.generic.library.dto.LoginRequest;
import com.generic.library.model.User;
import com.generic.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Processing authentication logic in service for user: {}", request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.error("User not found with email {}", request.email());
                    return new BadCredentialsException("User not found: " + request.email());
                });

        log.info("User found: {}", user);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        String jwtToken = jwtService.generateToken(extraClaims, user.getEmail());

        log.info("Authentication logic finished successfully for user: {}", user.getEmail());
        return new AuthResponse(jwtToken, "Bearer");
    }
}
