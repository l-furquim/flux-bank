package com.fluxbank.notification_service.domain.service;

import com.fluxbank.notification_service.interfaces.dto.PixkeyCreatedEventData;
import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;

public interface MailService {

    void sendPixReceived(TransactionNotificationEvent event,String valueFormated);
    void sendPixSent(TransactionNotificationEvent event,String valueFormated);
    void sendPixKeyCreated(PixkeyCreatedEventData event);
    void sendLimitExceeded();
    void sendPixSentFailed(TransactionNotificationEvent event, String valueFormated);

}
