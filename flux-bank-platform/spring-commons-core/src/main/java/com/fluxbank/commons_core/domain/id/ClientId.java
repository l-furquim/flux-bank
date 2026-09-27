package com.fluxbank.commons_core.domain.id;

import com.fluxbank.commons_core.domain.DomainId;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record ClientId(UUID value) implements DomainId {

    public static final String PREFIX = "cli";

    public ClientId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    public static ClientId parse(String raw) {
        return new ClientId(DomainIds.parse(PREFIX, raw));
    }

    public static Optional<ClientId> tryParse(String raw) {
        return DomainIds.tryParse(PREFIX, raw).map(ClientId::new);
    }
}
