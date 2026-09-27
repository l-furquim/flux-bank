package com.fluxbank.starter_context;

import com.fluxbank.starter_context.propagation.FluxContextThreadLocalAccessor;
import com.fluxbank.starter_context.service.FluxContextAccessor;
import com.fluxbank.starter_context.service.FluxContextHolder;
import com.fluxbank.starter_context.service.impl.DefaultFluxContextAccessor;
import io.micrometer.context.ContextRegistry;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(FluxContextProperties.class)
public class FluxContextAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    FluxContextAccessor fluxContextAccessor() {
        return new DefaultFluxContextAccessor();
    }

    @Bean
    @ConditionalOnMissingBean
    FluxContextHolder fluxContextHolder(FluxContextAccessor accessor) {
        return new FluxContextHolder(accessor);
    }

    @Bean
    SmartInitializingSingleton fluxContextPropagation(FluxContextAccessor accessor) {
        FluxContextThreadLocalAccessor threadLocalAccessor = new FluxContextThreadLocalAccessor(accessor);
        return () -> {
            try {
                ContextRegistry.getInstance().registerThreadLocalAccessor(threadLocalAccessor);
            } catch (IllegalArgumentException ex) {
                // The registry is JVM-wide and keeps the first accessor.
            }
        };
    }
}
