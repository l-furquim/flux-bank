package com.fluxbank.commons_events.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvents {

    @JsonProperty("event_id")
    UUID eventId();

    @JsonProperty("event_type")
    String eventType();

    @JsonProperty("occurred_at")
    Instant occurredAt();

    @JsonProperty("correlation_id")
    String correlationId();

    @JsonProperty("aggregate_id")
    String aggregateId();

}
