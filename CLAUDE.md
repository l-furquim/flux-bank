# Plataforma Spring

Os starters existem para tirar boilerplate dos serviços: contexto, headers, traces, erros HTTP e autenticação entram pela dependência, não por código copiado.

```text
commons-core
    ↑
starter-context
    ↑           ↑
commons-web   starter-security
```

`starter-security` não depende de `commons-web`. Os dois se encontram no serviço.

## Responsabilidade

- `commons-core` guarda primitivos sem Spring e sem HTTP: `Money`, `DomainId`, `FluxException` e geração de id. Códigos de erro concretos (`ACCOUNT_NOT_FOUND`, `INSUFFICIENT_BALANCE`) ficam no serviço.
- `starter-context` guarda `FluxContext`, o accessor e a propagação. Não conhece Servlet, Spring Security nem regra de negócio. O mesmo contexto vale para HTTP, Kafka, job e worker.
- `commons-web` cria o contexto a partir do HTTP, devolve request id e correlation id, e traduz exceção no `ProblemDetail`. Não autentica e não decide se uma conta pode ser movimentada.
- `starter-security` valida o JWT de entrada, produz `FluxPrincipal` e copia a identidade autenticada para o `FluxContext`. Não conhece account, customer ou card service.

## Contexto confiável

Headers de transporte (`X-Request-Id`, `X-Correlation-Id`, `X-Source`) podem entrar pelo filtro HTTP. `traceId` vem do OpenTelemetry, quando a API está no classpath.

`X-Account-Id` nunca vira contexto. Identidade (`userId`, `deviceId`, `sessionId`, `clientId`, `tenantId`) entra pelo principal autenticado. Headers de identidade só são aceitos com `fluxbank.context.trust-inbound-identity-headers=true`, para um gateway que já autenticou a chamada. O default é `false`.

`accountId` só muda quando o serviço chama `withAccountId`, depois de autorizar o acesso à conta.

## Como um serviço usa

A dependência liga o starter. Não existe `@EnableFluxBankSecurity`. Auto-configuração registra accessor, filtro, handler e a cadeia JWT.

```yaml
fluxbank:
  security:
    jwt:
      issuer: https://auth.fluxbank.local
      audience: fluxbank-api
```

Segredo de cliente não entra em código nem em config versionada.

## O que fica para depois

Observabilidade, Kafka, outbox e idempotência de negócio não fazem parte destes três starters. Idempotência HTTP e o registro de rotas no Redis só sobem com `fluxbank.idempotency.enabled` e `fluxbank.routes.enabled`. Propagação para Kafka e `@Async` usa o `FluxContextAccessor` e o `ThreadLocalAccessor` já registrado.
