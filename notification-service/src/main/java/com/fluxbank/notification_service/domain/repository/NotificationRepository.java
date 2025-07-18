package com.fluxbank.notification_service.domain.repository;

import com.fluxbank.notification_service.domain.model.Notification;

public interface NotificationRepository {

    Notification save(Notification notification);


}
