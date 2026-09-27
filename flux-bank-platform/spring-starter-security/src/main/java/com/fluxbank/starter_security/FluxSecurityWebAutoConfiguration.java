package com.fluxbank.starter_security;

import com.fluxbank.starter_context.service.FluxContextAccessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.Assert;

@AutoConfiguration(beforeName = {
        "org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration",
        "org.springframework.boot.security.oauth2.server.resource.autoconfigure.web.OAuth2ResourceServerWebSecurityAutoConfiguration"
})
@AutoConfigureAfter(FluxSecurityAutoConfiguration.class)
@ConditionalOnClass(name = "jakarta.servlet.Filter")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "fluxbank.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FluxSecurityWebAutoConfiguration {


    static final int ENRICHER_ORDER = -99;

    @Bean
    @ConditionalOnMissingBean
    JwtDecoder jwtDecoder(FluxSecurityProperties properties) {
        String issuer = properties.getJwt().getIssuer();
        Assert.hasText(issuer, "fluxbank.security.jwt.issuer must be set");
        NimbusJwtDecoder decoder = JwtDecoders.fromIssuerLocation(issuer);
        String audience = properties.getJwt().getAudience();
        if (audience == null || audience.isBlank()) {
            return decoder;
        }
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                token -> audienceMatches(token, audience)
        ));
        return decoder;
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain fluxSecurityFilterChain(
            HttpSecurity http,
            FluxJwtAuthenticationConverter converter
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
        return http.build();
    }

    @Bean
    FilterRegistrationBean<FluxSecurityContextEnricherFilter> fluxSecurityContextEnricherFilter(
            FluxContextAccessor accessor,
            FluxSecurityContextEnricher enricher
    ) {
        FilterRegistrationBean<FluxSecurityContextEnricherFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new FluxSecurityContextEnricherFilter(accessor, enricher));
        registration.setOrder(ENRICHER_ORDER);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean
    FluxSecurityMvcConfiguration fluxSecurityMvcConfiguration() {
        return new FluxSecurityMvcConfiguration();
    }

    private static OAuth2TokenValidatorResult audienceMatches(Jwt jwt, String audience) {
        if (jwt.getAudience() != null && jwt.getAudience().contains(audience)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "Required audience is missing", null)
        );
    }
}
