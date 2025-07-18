package com.fluxbank.notification_service.domain.exceptions;

public class FailedWhileSendingTheNotification extends RuntimeException {
    public FailedWhileSendingTheNotification(String message) {
        super(message);
    }
}
