package com.fluxbank.commons_events;

import com.fluxbank.commons_events.domain.DomainEvents;
import com.fluxbank.commons_events.transactions.TransactionSettledEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainEventsTest {

    @Test
    void settledEventExposesTheEnvelope() {
        UUID eventId = UUID.fromString("0199aabb-ccdd-7eef-8000-111122223333");
        DomainEvents event = new TransactionSettledEvent(
                eventId,
                "transaction.settled",
                Instant.EPOCH,
                "cor_0199aabbccdd7eef8000111122223333",
                "agg",
                "tx",
                "acc",
                "PIX",
                BigDecimal.ONE,
                "src",
                "dst",
                "user",
                "device"
        );

        assertEquals(eventId, event.eventId());
        assertEquals("cor_0199aabbccdd7eef8000111122223333", event.correlationId());
    }
}
