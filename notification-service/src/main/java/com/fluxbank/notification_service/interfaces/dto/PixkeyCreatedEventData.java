package com.fluxbank.notification_service.interfaces.dto;

import java.time.LocalDateTime;

public record PixkeyCreatedEventData(
        String userCpf,
        String userName,
        String userId,
        String userEmail,
        String key,
        String type,
        LocalDateTime issuedAt

) {
}
