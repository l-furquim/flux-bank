package com.fluxbank.starter_context.trace;

import java.util.Optional;

@FunctionalInterface
public interface TraceIdSupplier {

    Optional<String> current();

    static TraceIdSupplier none() {
        return Optional::empty;
    }
}
