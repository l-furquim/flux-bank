package com.fluxbank.user_service.domain.exceptions;

public class InvalidPixKeyCreationException extends RuntimeException {
    public InvalidPixKeyCreationException() {
        super("Please send an supported pix key.");
    }
}
