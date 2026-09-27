package com.fluxbank.commons_core.utils;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public final class UuidV7IdGenerator implements IdGenerator {

    @Override
    public UUID generate() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
