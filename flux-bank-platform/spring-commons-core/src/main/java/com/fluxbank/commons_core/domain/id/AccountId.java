package com.fluxbank.commons_core.domain.id;

import com.fluxbank.commons_core.domain.DomainId;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record AccountId(UUID value) implements DomainId {

    public static final String PREFIX = "acc";

    public AccountId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    public static AccountId parse(String raw) {
        return new AccountId(DomainIds.parse(PREFIX, raw));
    }

    public static Optional<AccountId> tryParse(String raw) {
        return DomainIds.tryParse(PREFIX, raw).map(AccountId::new);
    }
}
