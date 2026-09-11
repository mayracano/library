package com.generic.library.exception;

public class BookNoCopiesException extends RuntimeException {
    public BookNoCopiesException(String message) {
        super(message);
    }
}
