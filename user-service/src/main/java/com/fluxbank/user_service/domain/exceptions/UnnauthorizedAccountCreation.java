package com.fluxbank.user_service.domain.exceptions;

public class UnnauthorizedAccountCreation extends RuntimeException {
    public UnnauthorizedAccountCreation(String message) {
        super(message);
    }
}
