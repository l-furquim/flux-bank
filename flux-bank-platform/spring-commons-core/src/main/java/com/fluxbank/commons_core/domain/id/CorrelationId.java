package com.fluxbank.commons_core.domain.id;

import com.fluxbank.commons_core.domain.DomainId;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record CorrelationId(UUID value) implements DomainId {

    public static final String PREFIX = "cor";

    public CorrelationId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    public static CorrelationId parse(String raw) {
        return new CorrelationId(DomainIds.parse(PREFIX, raw));
    }

    public static Optional<CorrelationId> tryParse(String raw) {
        return DomainIds.tryParse(PREFIX, raw).map(CorrelationId::new);
    }
}
