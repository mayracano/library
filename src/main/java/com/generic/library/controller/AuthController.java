package com.generic.library.controller;

import com.generic.library.config.JwtService;
import com.generic.library.dto.AuthResponse;
import com.generic.library.dto.LoginRequest;
import com.generic.library.model.Book;
import com.generic.library.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @RequestMapping(method = RequestMethod.POST, path="/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST endpoint hit: User login attempt for {}", request.email());

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
