package com.fluxbank.notification_service.domain.model;

import com.fluxbank.notification_service.domain.enums.NotificationStatus;
import com.fluxbank.notification_service.domain.enums.NotificationTopic;
import com.fluxbank.notification_service.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    private UUID id;

    private String subject;

    private UUID userId;

    private NotificationType type;

    private NotificationTopic topic;

    private NotificationStatus status;

    private String title;

    private String content;

    private LocalDateTime sentAt;

    private UUID transactionId;

    private String transactionType;

    private String eventType;

    private BigDecimal amount;

    private String currency;

    private String description;

    private String account;

    private LocalDateTime transactionProcessedAt;

    private String failureReason;

    private LocalDateTime createdAt;

}
