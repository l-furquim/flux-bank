package com.fluxbank.starter_security;

import com.fluxbank.commons_core.domain.id.ClientId;
import com.fluxbank.commons_core.domain.id.DeviceId;
import com.fluxbank.commons_core.domain.id.SessionId;
import com.fluxbank.commons_core.domain.id.TenantId;
import com.fluxbank.commons_core.domain.id.UserId;
import com.fluxbank.starter_context.domain.FluxContext;

import java.util.Objects;

public final class FluxSecurityContextEnricher {

    public FluxContext enrich(FluxContext context, FluxPrincipal principal) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(principal, "principal");
        return context.withIdentity(
                UserId.tryParse(principal.userId()).orElse(null),
                DeviceId.tryParse(principal.deviceId()).orElse(null),
                SessionId.tryParse(principal.sessionId()).orElse(null),
                ClientId.tryParse(principal.clientId()).orElse(null),
                TenantId.tryParse(principal.tenantId()).orElse(null)
        );
    }
}
