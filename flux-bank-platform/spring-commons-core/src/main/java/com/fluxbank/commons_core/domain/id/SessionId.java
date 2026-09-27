package com.fluxbank.commons_core.domain.id;

import com.fluxbank.commons_core.domain.DomainId;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record SessionId(UUID value) implements DomainId {

    public static final String PREFIX = "ses";

    public SessionId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    public static SessionId parse(String raw) {
        return new SessionId(DomainIds.parse(PREFIX, raw));
    }

    public static Optional<SessionId> tryParse(String raw) {
        return DomainIds.tryParse(PREFIX, raw).map(SessionId::new);
    }
}
