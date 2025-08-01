package com.fluxbank.gateway_service.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTokenData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private String email;
    private String deviceId;
    private Instant issuedAt;
    private Instant expiresAt;
    private Map<String, Object> additionalClaims;

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public long getSecondsUntilExpiration() {
        if (expiresAt == null) return 0;
        return Duration.between(Instant.now(), expiresAt).getSeconds();
    }

}
