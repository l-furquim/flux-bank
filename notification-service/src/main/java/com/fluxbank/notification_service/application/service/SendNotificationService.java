package com.fluxbank.notification_service.application.service;

import com.fluxbank.notification_service.application.usecase.SendNotificationUsecase;
import com.fluxbank.notification_service.domain.repository.NotificationRepository;
import com.fluxbank.notification_service.interfaces.dto.SendNotificationEventDto;
import org.springframework.stereotype.Service;

@Service
public class SendNotificationService implements SendNotificationUsecase {

    private final NotificationRepository repository;

    public SendNotificationService(NotificationRepository repository) {
        this.repository = repository;
    }


    @Override
    public void send(SendNotificationEventDto data) {

    }
}
