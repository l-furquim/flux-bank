package com.fluxbank.starter_security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Objects;

public final class FluxAuthentication extends AbstractAuthenticationToken {

    private final FluxPrincipal principal;

    public FluxAuthentication(FluxPrincipal principal) {
        super(principal.authorities().stream().map(SimpleGrantedAuthority::new).toList());
        this.principal = Objects.requireNonNull(principal, "principal");
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public FluxPrincipal getPrincipal() {
        return principal;
    }
}
