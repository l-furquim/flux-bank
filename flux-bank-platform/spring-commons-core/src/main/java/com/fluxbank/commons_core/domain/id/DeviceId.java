package com.fluxbank.commons_core.domain.id;

import com.fluxbank.commons_core.domain.DomainId;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record DeviceId(UUID value) implements DomainId {

    public static final String PREFIX = "dev";

    public DeviceId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    public static DeviceId parse(String raw) {
        return new DeviceId(DomainIds.parse(PREFIX, raw));
    }

    public static Optional<DeviceId> tryParse(String raw) {
        return DomainIds.tryParse(PREFIX, raw).map(DeviceId::new);
    }
}
