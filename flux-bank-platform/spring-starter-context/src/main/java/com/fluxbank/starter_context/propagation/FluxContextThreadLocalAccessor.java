package com.fluxbank.starter_context.propagation;

import com.fluxbank.starter_context.domain.FluxContext;
import com.fluxbank.starter_context.service.FluxContextAccessor;
import io.micrometer.context.ThreadLocalAccessor;

import java.util.Objects;

public final class FluxContextThreadLocalAccessor implements ThreadLocalAccessor<FluxContext> {

    public static final String KEY = "fluxbank.context";

    private final FluxContextAccessor accessor;

    public FluxContextThreadLocalAccessor(FluxContextAccessor accessor) {
        this.accessor = Objects.requireNonNull(accessor, "accessor");
    }

    @Override
    public Object key() {
        return KEY;
    }

    @Override
    public FluxContext getValue() {
        return accessor.getOptional().orElse(null);
    }

    @Override
    public void setValue(FluxContext value) {
        if (value == null) {
            accessor.clear();
            return;
        }
        accessor.set(value);
    }

    @Override
    public void setValue() {
        accessor.clear();
    }
}
