package com.example.Rider.exception;

import java.io.Serial;

public class CustomExpiredJwtException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CustomExpiredJwtException() {
        super("JWT Token is expired!");
    }

    public CustomExpiredJwtException(String message) {
        super(message);
    }
}
