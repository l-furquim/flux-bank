package com.fluxbank.starter_context;

import com.fluxbank.starter_context.trace.OpenTelemetryTraceIdSupplier;
import com.fluxbank.starter_context.trace.TraceIdSupplier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(name = "io.opentelemetry.api.trace.Span")
public class OpenTelemetryTraceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    TraceIdSupplier traceIdSupplier() {
        return new OpenTelemetryTraceIdSupplier();
    }
}
