package com.fluxbank.starter_context.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

import java.util.Optional;

public final class OpenTelemetryTraceIdSupplier implements TraceIdSupplier {

    @Override
    public Optional<String> current() {
        SpanContext spanContext = Span.current().getSpanContext();
        if (!spanContext.isValid()) {
            return Optional.empty();
        }
        String traceId = spanContext.getTraceId();
        if (traceId == null || traceId.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(traceId);
    }
}
