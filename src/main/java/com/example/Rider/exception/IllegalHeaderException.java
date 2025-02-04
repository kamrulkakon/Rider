package com.example.Rider.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class IllegalHeaderException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public IllegalHeaderException(String message) {
        super(message);
    }
}

