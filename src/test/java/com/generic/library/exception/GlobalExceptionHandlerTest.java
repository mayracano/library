package com.generic.library.exception;

import com.generic.library.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/books/1");
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Book not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFound(exception, request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), response.getBody().error());
        assertEquals("Book not found", response.getBody().message());
        assertEquals("/api/books/1", response.getBody().path());
    }

    @Test
    void shouldHandleDuplicateResourceException() {
        DuplicateResourceException exception = new DuplicateResourceException("Book already exists");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals(HttpStatus.CONFLICT.getReasonPhrase(), response.getBody().error());
        assertEquals("Book already exists", response.getBody().message());
        assertEquals("/api/books/1", response.getBody().path());
    }

    @Test
    void shouldHandleBookNoCopiesException() {
        BookNoCopiesException exception = new BookNoCopiesException("No copies available");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBookNoCopiesException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), response.getBody().error());
        assertEquals("No copies available", response.getBody().message());
        assertEquals("/api/books/1", response.getBody().path());
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getMessage()).thenReturn("Validation failed for argument");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValidException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), response.getBody().error());
        assertTrue(response.getBody().message().contains("Validation failed"));
        assertEquals("/api/books/1", response.getBody().path());
    }

    @Test
    void shouldHandleBadCredentialsException() {
        BadCredentialsException badCredentialsException = mock(BadCredentialsException.class);
        when(badCredentialsException.getMessage()).thenReturn("Bad Credentials");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(badCredentialsException, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), response.getBody().error());
        assertTrue(response.getBody().message().contains("Bad Credentials"));
        assertEquals("/api/books/1", response.getBody().path());
    }
}
