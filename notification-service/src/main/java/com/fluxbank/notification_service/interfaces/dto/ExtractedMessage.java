package com.fluxbank.notification_service.interfaces.dto;

import com.fluxbank.notification_service.domain.enums.EventSource;

public record ExtractedMessage(
        String messageContent,
        EventSource eventSource,
        String topicArn,
        String messageId,
        String timestamp
) {}
