package com.fluxbank.starter_context.domain;

import com.fluxbank.commons_core.domain.id.AccountId;
import com.fluxbank.commons_core.domain.id.ClientId;
import com.fluxbank.commons_core.domain.id.CorrelationId;
import com.fluxbank.commons_core.domain.id.DeviceId;
import com.fluxbank.commons_core.domain.id.RequestId;
import com.fluxbank.commons_core.domain.id.SessionId;
import com.fluxbank.commons_core.domain.id.TenantId;
import com.fluxbank.commons_core.domain.id.UserId;

import java.util.Objects;

public record FluxContext(
        RequestId requestId,
        CorrelationId correlationId,
        String traceId,
        TenantId tenantId,
        AccountId accountId,
        UserId userId,
        DeviceId deviceId,
        SessionId sessionId,
        ClientId clientId,
        RequestSource source
) {

    public FluxContext {
        Objects.requireNonNull(requestId, "requestId");
        Objects.requireNonNull(correlationId, "correlationId");
        if (source == null) {
            source = RequestSource.UNKNOWN;
        }
    }

    public FluxContext withAccountId(AccountId accountId) {
        return new FluxContext(
                requestId,
                correlationId,
                traceId,
                tenantId,
                accountId,
                userId,
                deviceId,
                sessionId,
                clientId,
                source
        );
    }

    public FluxContext withIdentity(
            UserId userId,
            DeviceId deviceId,
            SessionId sessionId,
            ClientId clientId,
            TenantId tenantId
    ) {
        return new FluxContext(
                requestId,
                correlationId,
                traceId,
                tenantId,
                accountId,
                userId,
                deviceId,
                sessionId,
                clientId,
                source
        );
    }
}
