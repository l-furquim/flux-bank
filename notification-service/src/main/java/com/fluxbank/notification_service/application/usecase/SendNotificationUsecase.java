package com.fluxbank.notification_service.application.usecase;

import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;

public interface SendNotificationUsecase {

    void send(TransactionNotificationEvent data);

}
