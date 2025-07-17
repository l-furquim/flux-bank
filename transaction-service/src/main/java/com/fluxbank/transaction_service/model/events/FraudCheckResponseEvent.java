package com.fluxbank.transaction_service.model.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudCheckResponseEvent {

    private String id;
    private String type;
    private String transactionId;
    private String transactionType;
    private String status;
    private BigDecimal amount;
    private String currency;
    private long processingDurationMs;
    private Instant timestamp;
    private String sourceService;

}
