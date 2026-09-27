package com.fluxbank.starter_context;

import com.fluxbank.commons_core.domain.id.AccountId;
import com.fluxbank.commons_core.domain.id.CorrelationId;
import com.fluxbank.commons_core.domain.id.RequestId;
import com.fluxbank.starter_context.domain.FluxContext;
import com.fluxbank.starter_context.domain.RequestSource;
import com.fluxbank.starter_context.propagation.FluxContextThreadLocalAccessor;
import com.fluxbank.starter_context.service.FluxContextAccessor;
import com.fluxbank.starter_context.service.FluxContextHolder;
import com.fluxbank.starter_context.service.impl.DefaultFluxContextAccessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FluxContextTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    FluxContextAutoConfiguration.class,
                    OpenTelemetryTraceAutoConfiguration.class
            ));

    @Test
    void accessorStoresAndClearsContext() {
        FluxContextAccessor accessor = new DefaultFluxContextAccessor();
        FluxContext context = sample();

        assertThatThrownBy(accessor::get).isInstanceOf(IllegalStateException.class);

        accessor.set(context);
        assertThat(new FluxContextHolder(accessor).current()).isEqualTo(context);

        FluxContextThreadLocalAccessor propagated = new FluxContextThreadLocalAccessor(accessor);
        assertThat(propagated.getValue()).isEqualTo(context);
        propagated.setValue();
        assertThat(accessor.getOptional()).isEmpty();
    }

    @Test
    void accountIdDoesNotReplaceTransportFields() {
        AccountId accountId = new AccountId(UUID.randomUUID());
        FluxContext updated = sample().withAccountId(accountId);

        assertThat(updated.accountId()).isEqualTo(accountId);
        assertThat(updated.requestId()).isEqualTo(sample().requestId());
    }

    @Test
    void autoConfigurationRegistersAccessor() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(FluxContextAccessor.class);
            assertThat(context).hasSingleBean(FluxContextHolder.class);
            assertThat(context).hasSingleBean(FluxContextProperties.class);
        });
    }

    private static FluxContext sample() {
        return new FluxContext(
                new RequestId(UUID.fromString("0199aabb-ccdd-7eef-8000-111122223333")),
                new CorrelationId(UUID.fromString("0199aabb-ccdd-7eef-8000-111122223334")),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                RequestSource.UNKNOWN
        );
    }
}
