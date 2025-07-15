package com.fluxbank.transaction_service.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudCheckResponseEvent {

    private UUID id;
    private String type;
    private UUID transactionId;
    private String transactionType;
    private String status;
    private BigDecimal amount;
    private String currency;
    private long processingDurationMs;
    private Instant timestamp;
    private String sourceService;

}
