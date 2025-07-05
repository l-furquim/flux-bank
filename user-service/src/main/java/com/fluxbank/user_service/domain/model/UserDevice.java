package com.fluxbank.user_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDevice {

    private UUID id;

    private UUID userId;

    private String deviceId;

    private String userAgent;

}
