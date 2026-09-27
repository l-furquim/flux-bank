package com.fluxbank.commons_core.domain.id;

import com.fluxbank.commons_core.domain.DomainId;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record RequestId(UUID value) implements DomainId {

    public static final String PREFIX = "req";

    public RequestId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    public static RequestId parse(String raw) {
        return new RequestId(DomainIds.parse(PREFIX, raw));
    }

    public static Optional<RequestId> tryParse(String raw) {
        return DomainIds.tryParse(PREFIX, raw).map(RequestId::new);
    }
}
