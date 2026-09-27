package com.fluxbank.starter_context.service;

import com.fluxbank.starter_context.domain.FluxContext;

import java.util.Optional;

public interface FluxContextAccessor {

    FluxContext get();

    Optional<FluxContext> getOptional();

    void set(FluxContext context);

    void clear();
}
