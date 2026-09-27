package com.fluxbank.starter_security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class FluxJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    static final String PRINCIPAL_TYPE = "principal_type";
    static final String CLIENT_ID = "client_id";
    static final String AUTHORIZED_PARTY = "azp";
    static final String TENANT_ID = "tenant_id";
    static final String USER_ID = "user_id";
    static final String DEVICE_ID = "device_id";
    static final String SESSION_ID = "session_id";
    static final String SID = "sid";
    static final String AUTHORITIES = "authorities";
    static final String SCOPE = "scope";
    static final String SCP = "scp";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) {
            throw invalid("JWT subject is required");
        }

        return new FluxAuthentication(new FluxPrincipal(
                subject,
                principalType(jwt),
                authorities(jwt),
                firstText(jwt, CLIENT_ID, AUTHORIZED_PARTY),
                text(jwt, TENANT_ID),
                text(jwt, USER_ID),
                text(jwt, DEVICE_ID),
                firstText(jwt, SESSION_ID, SID)
        ));
    }

    private static PrincipalType principalType(Jwt jwt) {
        String raw = text(jwt, PRINCIPAL_TYPE);
        if (raw == null) {
            return PrincipalType.USER;
        }
        try {
            return PrincipalType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw invalid("Unsupported principal_type");
        }
    }

    private static Set<String> authorities(Jwt jwt) {
        LinkedHashSet<String> values = new LinkedHashSet<>();
        addClaim(values, jwt.getClaim(AUTHORITIES));
        addClaim(values, jwt.getClaim(SCOPE));
        addClaim(values, jwt.getClaim(SCP));
        return Set.copyOf(values);
    }

    private static void addClaim(Set<String> target, Object claim) {
        if (claim instanceof Collection<?> values) {
            for (Object value : values) {
                addToken(target, value);
            }
            return;
        }
        if (claim instanceof String text) {
            for (String part : text.split(" ")) {
                addToken(target, part);
            }
        }
    }

    private static void addToken(Set<String> target, Object value) {
        if (value == null) {
            return;
        }
        String text = value.toString().trim();
        if (!text.isEmpty()) {
            target.add(text);
        }
    }

    private static String firstText(Jwt jwt, String primary, String fallback) {
        String value = text(jwt, primary);
        return value != null ? value : text(jwt, fallback);
    }

    private static String text(Jwt jwt, String claim) {
        String value = jwt.getClaimAsString(claim);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private static OAuth2AuthenticationException invalid(String description) {
        return new OAuth2AuthenticationException(new OAuth2Error("invalid_token", description, null));
    }
}
