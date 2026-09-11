package com.generic.library.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock private JwtService jwtService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateWhenValidTokenIsProvided() throws Exception {
        String mockToken = "valid.jwt.token";
        String email = "admin@library.com";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + mockToken);
        when(jwtService.extractUsername(mockToken)).thenReturn(email);
        when(jwtService.isTokenValid(mockToken, email)).thenReturn(true);
        when(jwtService.extractRole(mockToken)).thenReturn("LIBRARIAN");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(email);
        assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_LIBRARIAN");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenNoAuthorizationHeaderIsPresent() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldFullyExecuteFilterAndAuthenticateUser() throws Exception {
        String mockToken = "token.jwt.valid";
        String userEmail = "admin@biblioteca.com";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + mockToken);
        when(jwtService.extractUsername(mockToken)).thenReturn(userEmail);
        when(jwtService.isTokenValid(mockToken, userEmail)).thenReturn(true);
        when(jwtService.extractRole(mockToken)).thenReturn("LIBRARIAN");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(userEmail);
        assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_LIBRARIAN");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldLogWarningWhenTokenIsInvalid() throws Exception {
        String mockToken = "token.invalido";
        String userEmail = "hacker@biblioteca.com";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + mockToken);
        when(jwtService.extractUsername(mockToken)).thenReturn(userEmail);
        when(jwtService.isTokenValid(mockToken, userEmail)).thenReturn(false); // Token inválido

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturnImmediatelyWhenHeaderDoesNotStartWithBearer() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNz");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldNotAuthenticateIfUserIsAlreadyAuthenticatedInContext() throws Exception {
        String mockToken = "token.valido";
        String userEmail = "admin@biblioteca.com";

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("already-auth", null, Collections.emptyList())
        );

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + mockToken);
        when(jwtService.extractUsername(mockToken)).thenReturn(userEmail);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).isTokenValid(anyString(), anyString());
    }

    @Test
    void shouldNotAuthenticateWhenExtractedUsernameIsNull() throws Exception {
        String mockToken = "token.mal-formado";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + mockToken);
        when(jwtService.extractUsername(mockToken)).thenReturn(null);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).isTokenValid(anyString(), anyString());
    }

}