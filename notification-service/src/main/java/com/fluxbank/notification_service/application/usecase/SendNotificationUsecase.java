package com.fluxbank.notification_service.application.usecase;

import com.fluxbank.notification_service.interfaces.dto.SendNotificationEventDto;

public interface SendNotificationUsecase {

    void send(SendNotificationEventDto data);

}
