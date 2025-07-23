package com.fluxbank.notification_service.infrastructure.persistence.entity;

import com.fluxbank.notification_service.domain.enums.NotificationStatus;
import com.fluxbank.notification_service.domain.enums.NotificationTopic;
import com.fluxbank.notification_service.domain.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "notifications")
public class NotificationEntity {

    @Id
    private UUID id;

    @NotBlank
    private String subject;

    @NotNull
    private UUID userId;

    private NotificationType type;

    private NotificationTopic topic;

    private NotificationStatus status;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
    private LocalDateTime sentAt;

    @Field("transaction_id")
    private UUID transactionId;

    @Field("transaction_type")
    private String transactionType;

    @Field("event_type")
    private String eventType;

    private BigDecimal amount;

    private String currency;

    private String description;

    private String account;

    @Field("transaction_processed_at")
    private LocalDateTime transactionProcessedAt;

    @Field("failure_reason")
    private String failureReason;

    @Field("created_at")
    private LocalDateTime createdAt;
}
