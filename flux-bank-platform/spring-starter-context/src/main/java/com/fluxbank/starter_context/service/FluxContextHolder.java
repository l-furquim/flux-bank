package com.fluxbank.starter_context.service;

import com.fluxbank.starter_context.domain.FluxContext;

import java.util.Objects;

public final class FluxContextHolder {

    private final FluxContextAccessor accessor;

    public FluxContextHolder(FluxContextAccessor accessor) {
        this.accessor = Objects.requireNonNull(accessor, "accessor");
    }

    public FluxContext current() {
        return accessor.get();
    }
}
