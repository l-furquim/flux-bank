package com.fluxbank.notification_service.domain.service;

import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;

public interface MailService {

    void sendPixReceived(TransactionNotificationEvent event);
    void sendPixSent(TransactionNotificationEvent event);
    void sendPixKeyCreated();
    void sendLimitExceeded();

}
