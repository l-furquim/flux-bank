package com.fluxbank.commons_web.filters;

import com.fluxbank.commons_core.domain.id.AccountId;
import com.fluxbank.commons_core.domain.id.UserId;
import com.fluxbank.commons_core.utils.IdGenerator;
import com.fluxbank.commons_web.exception.GlobalExceptionHandler;
import com.fluxbank.commons_web.exception.NotFoundException;
import com.fluxbank.commons_core.exception.FluxBusinessException;
import com.fluxbank.starter_context.FluxContextProperties;
import com.fluxbank.starter_context.domain.FluxHeaders;
import com.fluxbank.starter_context.service.impl.DefaultFluxContextAccessor;
import com.fluxbank.starter_context.trace.TraceIdSupplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FluxWebContractTest {

    @Test
    void filterGeneratesIdsAndIgnoresAccountHeader() throws Exception {
        DefaultFluxContextAccessor accessor = new DefaultFluxContextAccessor();
        FluxContextFilter filter = filter(accessor, false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(FluxHeaders.X_ACCOUNT_ID.value(), "acc_0199aabbccdd7eef8000111122223333");
        request.addHeader(FluxHeaders.X_USER_ID.value(), "usr_0199aabbccdd7eef8000111122223333");
        request.addHeader(FluxHeaders.X_REQUEST_ID.value(), "req_not-valid\n");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {
            assertThat(accessor.get().accountId()).isNull();
            assertThat(accessor.get().userId()).isNull();
            assertThat(accessor.get().requestId()._asString()).startsWith("req_");
            assertThat(accessor.get().correlationId()._asString()).startsWith("cor_");
        });

        assertThat(accessor.getOptional()).isEmpty();
        assertThat(response.getHeader(FluxHeaders.X_REQUEST_ID.value())).startsWith("req_");
        assertThat(response.getHeader(FluxHeaders.X_CORRELATION_ID.value())).startsWith("cor_");
    }

    @Test
    void trustedIdentityHeadersAreCopied() throws Exception {
        DefaultFluxContextAccessor accessor = new DefaultFluxContextAccessor();
        FluxContextFilter filter = filter(accessor, true);
        UserId userId = new UserId(UUID.fromString("0199aabb-ccdd-7eef-8000-111122223333"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(FluxHeaders.X_USER_ID.value(), userId._asString());
        request.addHeader(FluxHeaders.X_SOURCE.value(), "mobile");
        request.addHeader(FluxHeaders.X_ACCOUNT_ID.value(), new AccountId(UUID.randomUUID())._asString());

        filter.doFilter(request, new MockHttpServletResponse(), (req, res) -> {
            assertThat(accessor.get().userId()).isEqualTo(userId);
            assertThat(accessor.get().source().name()).isEqualTo("MOBILE");
            assertThat(accessor.get().accountId()).isNull();
        });
    }

    @Test
    void unexpectedErrorHidesInternalMessage() throws Exception {
        DefaultFluxContextAccessor accessor = new DefaultFluxContextAccessor();
        GlobalExceptionHandler handler = new GlobalExceptionHandler(accessor);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/entries");

        ResponseEntity<ProblemDetail> response = handler.handleUnexpected(
                new IllegalStateException("sql password=secret"),
                request
        );

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).isEqualTo("Erro interno inesperado");
        assertThat(response.getBody().getDetail()).doesNotContain("secret");
        assertThat(response.getBody().getTitle()).isEqualTo("internal-error");
    }

    @Test
    void businessExceptionUsesCodeWithoutHttpType() throws Exception {
        DefaultFluxContextAccessor accessor = new DefaultFluxContextAccessor();
        GlobalExceptionHandler handler = new GlobalExceptionHandler(accessor);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/entries");

        ResponseEntity<ProblemDetail> response = handler.handleFluxException(
                new FluxBusinessException("INSUFFICIENT_BALANCE", "Insufficient balance"),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody().getTitle()).isEqualTo("INSUFFICIENT_BALANCE");
    }

    @Test
    void domainExceptionKeepsHttpStatus() throws Exception {
        DefaultFluxContextAccessor accessor = new DefaultFluxContextAccessor();
        GlobalExceptionHandler handler = new GlobalExceptionHandler(accessor);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/entries");

        ResponseEntity<ProblemDetail> response = handler.handleDomainException(
                new NotFoundException("account-not-found", "Account not found"),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("account-not-found");
    }

    @SuppressWarnings("unchecked")
    private static FluxContextFilter filter(DefaultFluxContextAccessor accessor, boolean trustIdentity) {
        FluxContextProperties properties = new FluxContextProperties();
        properties.setTrustInboundIdentityHeaders(trustIdentity);
        ObjectProvider<TraceIdSupplier> provider = mock(ObjectProvider.class);
        org.mockito.Mockito.when(provider.getIfAvailable(org.mockito.ArgumentMatchers.any()))
                .thenReturn(TraceIdSupplier.none());
        return new FluxContextFilter(accessor, new SequentialIds(), provider, properties);
    }

    private static final class SequentialIds implements IdGenerator {
        private long sequence = 1;

        @Override
        public UUID generate() {
            return UUID.fromString("0199aabb-ccdd-7eef-8000-11112222333" + sequence++);
        }
    }
}
