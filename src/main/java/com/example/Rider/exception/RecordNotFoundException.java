package com.example.Rider.exception;

import java.io.Serial;

public class RecordNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public RecordNotFoundException() {
        super("No Resource Found!");
    }
    public RecordNotFoundException(String message) {
        super(message);
    }
}