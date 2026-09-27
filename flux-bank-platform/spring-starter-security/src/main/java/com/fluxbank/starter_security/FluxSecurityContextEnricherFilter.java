package com.fluxbank.starter_security;

import com.fluxbank.starter_context.service.FluxContextAccessor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class FluxSecurityContextEnricherFilter extends OncePerRequestFilter {

    private final FluxContextAccessor accessor;
    private final FluxSecurityContextEnricher enricher;

    public FluxSecurityContextEnricherFilter(
            FluxContextAccessor accessor,
            FluxSecurityContextEnricher enricher
    ) {
        this.accessor = accessor;
        this.enricher = enricher;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof FluxPrincipal principal) {
            accessor.getOptional().ifPresent(context -> accessor.set(enricher.enrich(context, principal)));
        }
        filterChain.doFilter(request, response);
    }
}
