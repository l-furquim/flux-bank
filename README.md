# FluxBank - Sistema de Pagamentos PIX

[![My Skills](https://skillicons.dev/icons?i=spring,redis,terraform,postgresql,aws,mongo,docker,prometheus,grafana)](https://skillicons.dev) 

FluxBank é uma plataforma de pagamentos instantâneos feita utilizando microserviços, implementando um sistema PIX. A arquitetura utiliza padrões de desenvolvimento com Spring Boot, comunicação assíncrona via eventos e infraestrutura na AWS.

## 🏗️ Arquitetura do Sistema

### Visão Geral
O sistema é composto por 6 microserviços independentes que se comunicam através de eventos (SNS/SQS) e APIs REST, seguindo princípios de Domain-Driven Design e Event-Driven Architecture.

```
┌─────────────────┐       ┌──────────────────┐       ┌─────────────────┐
│   Client Apps   │──────▶│  Gateway Service │──────▶│   User Service  │
└─────────────────┘       └──────────────────┘       └─────────────────┘
                                                          │
                                                          ▼
                                                   ┌──────────────────┐
                                                   │Transaction Service│
                                                   └──────────────────┘
                                                          │
                                                          ▼
                                                   ┌─────────────────┐
                                                   │  Wallet Service │
                                                   └─────────────────┘
                                                          │
                                                          ▼
                                                   ┌──────────────────┐
                                                   │   Fraud Service   │
                                                   └──────────────────┘
                                                          │
                                                          ▼
                                                   ┌────────────────────────┐
                                                   │ Notification Service   │
                                                   └────────────────────────┘

```

## Stack Usada

### Framework Base
- **Java**
- **Spring Boot**

### Banco de Dados
- **PostgreSQL** - Banco principal (User, Wallet, Transaction Services)
- **MongoDB** - Dados de análise (Fraud, Notification Services)
- **Redis** - Cache e sessões (Gateway, User, Wallet Services)
- **Flyway** - Migração de banco de dados

### Mensageria e Eventos
- **AWS SNS** - Publish/Subscribe de eventos
- **AWS SQS** - Filas de mensagens com DLQ

### Cloud e Infraestrutura
- **AWS ECS** - Infraestrutura dos containers
- **AWS Lambda** - Processamento serverless (detecção de fraude)
- **Terraform** - Infrastructure como codigo 
- **Docker Compose** - Ambiente de desenvolvimento
- **Prometheus** - Métricas e monitoramento

### Segurança e Resilência
- **Spring Security** - Autenticação e autorização
- **JWT (Auth0)** - Tokens de autenticação
- **Resilience4j** - Circuit breaker
- **Spring Cloud OpenFeign** - Cliente HTTP

### Documentação e Testes
- **SpringDoc OpenAPI** - Documentação automática da API
- **JUnit e mockito** - Testes unitários e integração

## Serviços criados

### 1. Gateway Service (Porta de Entrada)
**Responsabilidades:**
- API Gateway centralizado
- Autenticação JWT
- Roteamento para microserviços
- Cache de sessões no Redis
- Injeção de contexto do usuário

**Tecnologias específicas:**
- Spring WebFlux
- Spring Cloud Gateway
- Redis para cache

### 2. User Service (Gestão de Usuários)
**Responsabilidades:**
- Cadastro e autenticação de usuários
- Gestão de chaves PIX (email, CPF, telefone, aleatória)
- Resolução de chaves PIX
- Gestão de dispositivos do usuário

**Endpoints principais:**
- `POST /api/v1/users/register` - Cadastro
- `POST /api/v1/users/auth` - Autenticação
- `POST /api/v1/users/pix-keys` - Criação de chaves PIX
- `GET /api/v1/users/pix-keys/{key}` - Resolução de chaves

**Banco de dados:** PostgreSQL (users, pix_keys, user_devices)

### 3. Wallet Service (Carteira Digital)
**Responsabilidades:**
- Criação e gestão de carteiras digitais
- Operações de depósito e saque
- Controle de saldos e valores bloqueados
- Gestão de limites transacionais
- Histórico de transações da carteira

**Endpoints principais:**
- `POST /api/v1/wallets/create` - Criação de carteira
- `POST /api/v1/wallets/deposit` - Depósito
- `POST /api/v1/wallets/withdraw` - Saque
- `GET /api/v1/wallets/balance` - Consulta de saldo

**Banco de dados:** PostgreSQL (wallets, wallet_transactions, wallet_limits)

### 4. Transaction Service (Processamento de Transações)
**Responsabilidades:**
- Orquestração de transações PIX
- Validação de chaves PIX
- Integração com serviços de usuário e carteira
- Controle de estado das transações
- Histórico transacional

**Endpoints principais:**
- `POST /api/v1/transactions/pix/send` - Envio PIX
- `GET /api/v1/transactions/history` - Histórico das transações

**Integrações:**
- User Service (Feign Client) - Resolução de chaves PIX
- Wallet Service (Feign Client) - Operações de carteira
- Fraud Service (eventos) - Análise de fraude

**Banco de dados:** PostgreSQL + MongoDB (transações e eventos)

### 5. Fraud Service (Detecção de Fraudes)
**Responsabilidades:**
- Integração com AWS Lambda para simulação de detecção de fraude

**Padrão de comunicação:**
- Consome: `transaction.initiated`
- Produz: `fraud.check.completed`

**Banco de dados:** MongoDB (fraud_history)

### 6. Notification Service (Notificações)
**Responsabilidades:**
- Notificações multi-canal (email, push)
- Templates de notificação
- Controle de status de entrega
- Histórico de notificações

**Padrão de comunicação:**
- Consome: eventos de conclusão de transações e chaves pix criadas

**Banco de dados:** MongoDB (notifications)

### Fluxo PIX Completo
```
1. User inicia PIX → Transaction Service
2. TransactionInitiated event → SNS
3. Fraud Service consome evento → Analisa risco
4. FraudCheckCompleted event → SNS
5. Transaction Service consome → Processa se aprovado
6. Wallet Service → Atualiza saldos e limites
7. TransactionCompleted event → SNS
8. Notification Service → Envia push notification ou email
9. Analytics Service → Atualiza métricas
```

### Eventos do Sistema
- `transaction.initiated` - Transação iniciada
- `fraud.check.completed` - Análise de fraude concluída
- `transaction.completed` - Transação concluída com sucesso
- `transaction.failed` - Transação falhada
- `pix-key-created` - Criação de chave pix
- `limit-exceeded` - Limite de transações excedido

## Execução do Ambiente

# Subir infraestrutura local
```bash
docker-compose up -d
```
```bash
cd infra
terraform init
terraform plan
terraform apply
```

## Monitoramento

- **Prometheus**: Métricas de aplicação
- **Spring Actuator**: Health checks e informações da aplicação


## Estrutura de Banco de Dados

### PostgreSQL (Dados Transacionais)
- **user-service**: users, pix_keys, user_devices
- **wallet-service**: wallets, wallet_transactions, wallet_limits
- **transaction-service**: transactions

### MongoDB (Dados Analíticos)
- **fraud-service**: fraud_history
- **notification-service**: notifications
- **transaction-service**: transaction_events

### Redis (Cache)
- **gateway-service**: user_sessions, token_cache
- **user-service**: user_cache, device_cache
- **wallet-service**: balance_cache

## Padrões Arquiteturais Implementados

- **Microserviços** distribuindo a aplicações em serviços separados
- **Clean Architecture** para separar as regras de negócio dos frameworks e ferramentas utilizadas
- **Event-Driven Architecture** para comunicação assíncrona
- **CQRS** separando comandos de consultas
- **Circuit Breaker** para resilência
- **API Gateway** para entrada centralizada
- **Database per Service** com persistência poliglota

