package com.simple.identity.exception;

public class AccountNotActivatedException extends RuntimeException {
    public AccountNotActivatedException(String message) {
        super(message);
    }
}
