package com.fluxbank.commons_core.domain.id;

import com.fluxbank.commons_core.domain.DomainId;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record TenantId(UUID value) implements DomainId {

    public static final String PREFIX = "tnt";

    public TenantId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    public static TenantId parse(String raw) {
        return new TenantId(DomainIds.parse(PREFIX, raw));
    }

    public static Optional<TenantId> tryParse(String raw) {
        return DomainIds.tryParse(PREFIX, raw).map(TenantId::new);
    }
}
