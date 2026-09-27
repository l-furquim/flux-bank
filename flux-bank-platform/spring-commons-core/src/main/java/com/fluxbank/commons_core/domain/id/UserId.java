package com.fluxbank.commons_core.domain.id;

import com.fluxbank.commons_core.domain.DomainId;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record UserId(UUID value) implements DomainId {

    public static final String PREFIX = "usr";

    public UserId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    public static UserId parse(String raw) {
        return new UserId(DomainIds.parse(PREFIX, raw));
    }

    public static Optional<UserId> tryParse(String raw) {
        return DomainIds.tryParse(PREFIX, raw).map(UserId::new);
    }
}
