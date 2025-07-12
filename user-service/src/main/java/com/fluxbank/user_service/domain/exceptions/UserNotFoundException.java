package com.fluxbank.user_service.domain.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Could not found the user specified.");
    }
}
