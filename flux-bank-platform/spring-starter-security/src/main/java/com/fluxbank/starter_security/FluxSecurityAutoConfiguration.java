package com.fluxbank.starter_security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "fluxbank.security", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(FluxSecurityProperties.class)
public class FluxSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    FluxJwtAuthenticationConverter fluxJwtAuthenticationConverter() {
        return new FluxJwtAuthenticationConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    FluxSecurityContextEnricher fluxSecurityContextEnricher() {
        return new FluxSecurityContextEnricher();
    }
}
