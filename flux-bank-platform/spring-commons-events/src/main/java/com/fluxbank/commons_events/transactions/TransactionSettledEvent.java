package com.fluxbank.commons_events.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fluxbank.commons_events.domain.DomainEvents;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionSettledEvent(

        @JsonProperty("event_id") UUID eventId,
        @JsonProperty("event_type") String eventType,
        @JsonProperty("occurred_at") Instant occurredAt,
        @JsonProperty("correlation_id") String correlationId,
        @JsonProperty("aggregate_id") String aggregateId,

        @JsonProperty("transaction_id") String transactionId,
        @JsonProperty("account_id") String accountId,
        @JsonProperty("type") String type,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("source_account_id") String sourceAccountId,
        @JsonProperty("destination_account_id") String destinationAccountId,
        @JsonProperty("initiated_by_user_id") String initiatedByUserId,
        @JsonProperty("initiated_by_device_id") String initiatedByDeviceId

) implements DomainEvents {
}
