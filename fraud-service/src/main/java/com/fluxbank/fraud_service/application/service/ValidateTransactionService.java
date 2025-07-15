package com.fluxbank.fraud_service.application.service;

import com.fluxbank.fraud_service.application.usecase.ValidateTransactionUsecase;
import com.fluxbank.fraud_service.domain.exceptions.InvokeLambdaFunctionException;
import com.fluxbank.fraud_service.domain.service.LambdaService;
import com.fluxbank.fraud_service.infrastructure.messaging.producer.FraudCheckCompletedProducer;
import com.fluxbank.fraud_service.interfaces.dto.FraudAnalysisResponse;
import com.fluxbank.fraud_service.interfaces.dto.TransactionEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ValidateTransactionService implements ValidateTransactionUsecase {

    private final LambdaService lambdaService;
    private final FraudCheckCompletedProducer producer;

    public ValidateTransactionService(LambdaService lambdaService, FraudCheckCompletedProducer producer) {
        this.lambdaService = lambdaService;
        this.producer = producer;
    }

    @Override
    public void validate(TransactionEventDto event) {
        FraudAnalysisResponse response =  lambdaService.invoke(event);

        log.info("Response received from lambda function: {}", response);

        producer.publish(
                response
        );
    }
}
