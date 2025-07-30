package com.fluxbank.notification_service.application.usecase;

import com.fluxbank.notification_service.interfaces.dto.PixkeyCreatedEventData;
import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;

public interface SendNotificationUsecase {

    void sendTransactionUsecase(TransactionNotificationEvent data);
    void sendPixkeyCreatedUsecase(PixkeyCreatedEventData data);

}
