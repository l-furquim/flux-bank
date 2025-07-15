package com.fluxbank.fraud_service.domain.service;

import com.fluxbank.fraud_service.interfaces.dto.FraudAnalysisResponse;
import com.fluxbank.fraud_service.interfaces.dto.TransactionEventDto;

public interface LambdaService{

    FraudAnalysisResponse invoke(TransactionEventDto request);

}
