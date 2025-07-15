package com.fluxbank.fraud_service.interfaces.dto;

import com.fluxbank.fraud_service.domain.enums.FraudType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudAnalysisResponse{

    private UUID id;
    private FraudType type;
    private UUID transactionId;
    private String transactionType;
    private String status;
    private BigDecimal amount;
    private String currency;
    private long processingDurationMs;
    private Instant timestamp;
    private String sourceService;

}
