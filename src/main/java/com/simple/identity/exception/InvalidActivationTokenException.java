package com.simple.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidActivationTokenException extends RuntimeException {
    public InvalidActivationTokenException(String message) {
        super(message);
    }
}
