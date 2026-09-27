package com.fluxbank.starter_context.service.impl;

import com.fluxbank.starter_context.domain.FluxContext;
import com.fluxbank.starter_context.service.FluxContextAccessor;

import java.util.Objects;
import java.util.Optional;

public final class DefaultFluxContextAccessor implements FluxContextAccessor {

    private final ThreadLocal<FluxContext> context = new ThreadLocal<>();

    @Override
    public FluxContext get() {
        FluxContext value = context.get();
        if (value == null) {
            throw new IllegalStateException("No FluxContext available");
        }
        return value;
    }

    @Override
    public Optional<FluxContext> getOptional() {
        return Optional.ofNullable(context.get());
    }

    @Override
    public void set(FluxContext context) {
        this.context.set(Objects.requireNonNull(context, "context"));
    }

    @Override
    public void clear() {
        context.remove();
    }
}
