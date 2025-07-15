package com.fluxbank.fraud_service.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.fraud_service.application.usecase.ValidateTransactionUsecase;
import com.fluxbank.fraud_service.interfaces.dto.TransactionEventDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionInitiatedConsumer {

    private final ObjectMapper mapper;
    private final ValidateTransactionUsecase usecase;

    public TransactionInitiatedConsumer(ObjectMapper mapper, ValidateTransactionUsecase usecase) {
        this.mapper = mapper;
        this.usecase = usecase;
    }

    @SqsListener("fraud")
    public void listen(String message){
        try {
            log.info("Event received: {}", message);

            JsonNode node = mapper.readTree(message);

            String messageJson = node.get("Message").asText();

            TransactionEventDto event = mapper.readValue(messageJson, TransactionEventDto.class);

            log.info("Transaction event received: {}", event);

            usecase.validate(event);

        } catch (Exception e) {
            log.error("Error while consuming the transaction.initiated message: {}", e.getMessage());
        }
    }

}
