package com.fluxbank.commons_web.filters;

import com.fluxbank.commons_core.domain.id.ClientId;
import com.fluxbank.commons_core.domain.id.CorrelationId;
import com.fluxbank.commons_core.domain.id.DeviceId;
import com.fluxbank.commons_core.domain.id.DomainIds;
import com.fluxbank.commons_core.domain.id.RequestId;
import com.fluxbank.commons_core.domain.id.SessionId;
import com.fluxbank.commons_core.domain.id.TenantId;
import com.fluxbank.commons_core.domain.id.UserId;
import com.fluxbank.commons_core.utils.IdGenerator;
import com.fluxbank.starter_context.FluxContextProperties;
import com.fluxbank.starter_context.domain.FluxContext;
import com.fluxbank.starter_context.domain.FluxHeaders;
import com.fluxbank.starter_context.domain.RequestSource;
import com.fluxbank.starter_context.service.FluxContextAccessor;
import com.fluxbank.starter_context.trace.TraceIdSupplier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class FluxContextFilter extends OncePerRequestFilter {

    public static final String MDC_REQUEST_ID = "requestId";
    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String MDC_TRACE_ID = "traceId";

    private final FluxContextAccessor accessor;
    private final IdGenerator idGenerator;
    private final TraceIdSupplier traceIdSupplier;
    private final FluxContextProperties properties;

    public FluxContextFilter(
            FluxContextAccessor accessor,
            IdGenerator idGenerator,
            ObjectProvider<TraceIdSupplier> traceIdSupplier,
            FluxContextProperties properties
    ) {
        this.accessor = accessor;
        this.idGenerator = idGenerator;
        this.traceIdSupplier = traceIdSupplier.getIfAvailable(TraceIdSupplier::none);
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        RequestId requestId = RequestId.tryParse(request.getHeader(FluxHeaders.X_REQUEST_ID.value()))
                .orElseGet(() -> new RequestId(idGenerator.generate()));
        CorrelationId correlationId = CorrelationId.tryParse(request.getHeader(FluxHeaders.X_CORRELATION_ID.value()))
                .orElseGet(() -> new CorrelationId(idGenerator.generate()));
        String traceId = traceIdSupplier.current().filter(DomainIds::isSafeHeader).orElse(null);

        FluxContext context = new FluxContext(
                requestId,
                correlationId,
                traceId,
                trusted(request, FluxHeaders.X_TENANT_ID, TenantId::tryParse),
                null,
                trusted(request, FluxHeaders.X_USER_ID, UserId::tryParse),
                trusted(request, FluxHeaders.X_DEVICE_ID, DeviceId::tryParse),
                trusted(request, FluxHeaders.X_SESSION_ID, SessionId::tryParse),
                trusted(request, FluxHeaders.X_CLIENT_ID, ClientId::tryParse),
                RequestSource.fromHeader(request.getHeader(FluxHeaders.X_SOURCE.value()))
        );

        accessor.set(context);
        MDC.put(MDC_REQUEST_ID, requestId._asString());
        MDC.put(MDC_CORRELATION_ID, correlationId._asString());
        if (traceId != null) {
            MDC.put(MDC_TRACE_ID, traceId);
        }
        response.setHeader(FluxHeaders.X_REQUEST_ID.value(), requestId._asString());
        response.setHeader(FluxHeaders.X_CORRELATION_ID.value(), correlationId._asString());

        try {
            filterChain.doFilter(request, response);
        } finally {
            accessor.clear();
            MDC.remove(MDC_REQUEST_ID);
            MDC.remove(MDC_CORRELATION_ID);
            MDC.remove(MDC_TRACE_ID);
        }
    }

    private <T> T trusted(HttpServletRequest request, FluxHeaders header, Function<String, Optional<T>> parser) {
        if (!properties.isTrustInboundIdentityHeaders()) {
            return null;
        }
        return parser.apply(request.getHeader(header.value())).orElse(null);
    }
}
