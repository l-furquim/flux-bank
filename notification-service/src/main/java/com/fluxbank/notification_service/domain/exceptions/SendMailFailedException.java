package com.fluxbank.notification_service.domain.exceptions;

public class SendMailFailedException extends RuntimeException {
    public SendMailFailedException(String message) {
        super(message);
    }
}
