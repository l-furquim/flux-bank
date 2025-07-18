package com.fluxbank.notification_service.infrastructure.persistence.entity;

import com.fluxbank.notification_service.domain.enums.NotificationStatus;
import com.fluxbank.notification_service.domain.enums.NotificationTopic;
import com.fluxbank.notification_service.domain.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Document(collection = "notifications")
public class NotificationEntity {

    @Id
    private UUID id;

    @NotBlank
    private String subject;

    @NotNull
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationTopic topic;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private LocalDateTime sentAt;

}
