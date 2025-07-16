package com.fluxbank.transaction_service.model.events;


import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "transaction_events")
public class TransactionEvent {

    @Id
    private UUID id;

    @NotNull
    private String type;

    @NotNull
    private UUID transactionId;

    @NotNull
    private String transactionType;

    @NotNull
    private String status;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    private Long processingDurationMs;

    @NotNull
    private Instant timestamp;

    @NotNull
    private String sourceService;

}
