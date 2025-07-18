package com.fluxbank.notification_service.interfaces.dto;

import com.fluxbank.notification_service.domain.enums.NotificationTopic;
import com.fluxbank.notification_service.domain.enums.NotificationType;

public record SendNotificationEventDto(
        NotificationType type,
        NotificationTopic topic,
        String userId,
        String subject
) {
}
