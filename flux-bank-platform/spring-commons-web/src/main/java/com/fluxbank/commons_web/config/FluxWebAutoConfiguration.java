package com.fluxbank.commons_web.config;

import com.fluxbank.commons_core.utils.IdGenerator;
import com.fluxbank.commons_core.utils.UuidV7IdGenerator;
import com.fluxbank.commons_web.exception.GlobalExceptionHandler;
import com.fluxbank.commons_web.filters.FluxContextFilter;
import com.fluxbank.starter_context.FluxContextAutoConfiguration;
import com.fluxbank.starter_context.FluxContextProperties;
import com.fluxbank.starter_context.service.FluxContextAccessor;
import com.fluxbank.starter_context.trace.TraceIdSupplier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@AutoConfigureAfter(FluxContextAutoConfiguration.class)
public class FluxWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    IdGenerator idGenerator() {
        return new UuidV7IdGenerator();
    }

    @Bean
    FilterRegistrationBean<FluxContextFilter> fluxContextFilter(
            FluxContextAccessor accessor,
            IdGenerator idGenerator,
            ObjectProvider<TraceIdSupplier> traceIdSupplier,
            FluxContextProperties properties
    ) {
        FilterRegistrationBean<FluxContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new FluxContextFilter(accessor, idGenerator, traceIdSupplier, properties));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean
    GlobalExceptionHandler globalExceptionHandler(FluxContextAccessor accessor) {
        return new GlobalExceptionHandler(accessor);
    }
}
