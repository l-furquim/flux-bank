package com.fluxbank.starter_security;

import java.security.Principal;
import java.util.Objects;
import java.util.Set;

public record FluxPrincipal(
        String subject,
        PrincipalType type,
        Set<String> authorities,
        String clientId,
        String tenantId,
        String userId,
        String deviceId,
        String sessionId
) implements Principal {

    public FluxPrincipal {
        Objects.requireNonNull(subject, "subject");
        Objects.requireNonNull(type, "type");
        authorities = authorities == null ? Set.of() : Set.copyOf(authorities);
    }

    @Override
    public String getName() {
        return subject;
    }
}
