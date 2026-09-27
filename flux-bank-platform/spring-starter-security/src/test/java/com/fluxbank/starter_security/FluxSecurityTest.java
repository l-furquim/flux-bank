package com.fluxbank.starter_security;

import com.fluxbank.commons_core.domain.id.AccountId;
import com.fluxbank.commons_core.domain.id.ClientId;
import com.fluxbank.commons_core.domain.id.CorrelationId;
import com.fluxbank.commons_core.domain.id.RequestId;
import com.fluxbank.commons_core.domain.id.UserId;
import com.fluxbank.starter_context.domain.FluxContext;
import com.fluxbank.starter_context.domain.RequestSource;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FluxSecurityTest {

    private final FluxJwtAuthenticationConverter converter = new FluxJwtAuthenticationConverter();
    private final FluxSecurityContextEnricher enricher = new FluxSecurityContextEnricher();

    @Test
    void converterReadsServicePrincipalAndIgnoresAccountClaim() {
        UserId userId = new UserId(UUID.fromString("0199aabb-ccdd-7eef-8000-111122223333"));
        ClientId clientId = new ClientId(UUID.fromString("0199aabb-ccdd-7eef-8000-111122223334"));
        Jwt jwt = jwt()
                .subject("account-service")
                .claim(FluxJwtAuthenticationConverter.PRINCIPAL_TYPE, "SERVICE")
                .claim(FluxJwtAuthenticationConverter.CLIENT_ID, clientId._asString())
                .claim(FluxJwtAuthenticationConverter.USER_ID, userId._asString())
                .claim(FluxJwtAuthenticationConverter.AUTHORITIES, List.of("transfer:create"))
                .claim("account_id", "acc_0199aabbccdd7eef8000111122223333")
                .build();

        FluxPrincipal principal = ((FluxAuthentication) converter.convert(jwt)).getPrincipal();

        assertThat(principal.subject()).isEqualTo("account-service");
        assertThat(principal.type()).isEqualTo(PrincipalType.SERVICE);
        assertThat(principal.authorities()).containsExactly("transfer:create");
        assertThat(principal.clientId()).isEqualTo(clientId._asString());
        assertThat(principal.userId()).isEqualTo(userId._asString());
    }

    @Test
    void enricherCopiesTypedIdentityAndKeepsAccount() {
        AccountId accountId = new AccountId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        FluxContext context = context().withAccountId(accountId);
        FluxPrincipal principal = new FluxPrincipal(
                "account-service",
                PrincipalType.SERVICE,
                java.util.Set.of(),
                "account-service",
                null,
                userId._asString(),
                null,
                null
        );

        FluxContext enriched = enricher.enrich(context, principal);

        assertThat(enriched.accountId()).isEqualTo(accountId);
        assertThat(enriched.userId()).isEqualTo(userId);
        assertThat(enriched.clientId()).isNull();
        assertThat(enriched.requestId()).isEqualTo(context.requestId());
    }

    @Test
    void unsupportedPrincipalTypeIsRejected() {
        Jwt jwt = jwt()
                .subject("someone")
                .claim(FluxJwtAuthenticationConverter.PRINCIPAL_TYPE, "PARTNER")
                .build();

        assertThatThrownBy(() -> converter.convert(jwt)).isInstanceOf(OAuth2AuthenticationException.class);
    }

    private static Jwt.Builder jwt() {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.parse("2026-09-27T00:00:00Z"))
                .expiresAt(Instant.parse("2026-09-27T01:00:00Z"));
    }

    private static FluxContext context() {
        return new FluxContext(
                new RequestId(UUID.fromString("0199aabb-ccdd-7eef-8000-111122223331")),
                new CorrelationId(UUID.fromString("0199aabb-ccdd-7eef-8000-111122223332")),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                RequestSource.SERVICE
        );
    }
}
