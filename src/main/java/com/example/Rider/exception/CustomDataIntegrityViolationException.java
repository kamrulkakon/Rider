package com.example.Rider.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.CONFLICT)
public class CustomDataIntegrityViolationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CustomDataIntegrityViolationException(String string) {
        super(string);
    }
}
