package com.fluxbank.transaction_service.messaging.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.model.events.FraudCheckResponseEvent;
import com.fluxbank.transaction_service.service.TransactionService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FraudCheckCompletedConsumer {

    private final ObjectMapper mapper;
    private final TransactionService transactionService;

    public FraudCheckCompletedConsumer(ObjectMapper mapper, TransactionService transactionService) {
        this.mapper = mapper;
        this.transactionService = transactionService;
    }

    @SqsListener("transaction")
    public void listen(String message){
        try {
            log.info("Event received: {}", message);

            JsonNode node = mapper.readTree(message);

            String messageJson = node.get("Message").asText();

            FraudCheckResponseEvent event = mapper.readValue(messageJson, FraudCheckResponseEvent.class);

            log.info("Fraud check event received: {}", event);

            log.info("Fraud DTO class: {}", (Object) FraudCheckResponseEvent.class.getDeclaredFields());

            transactionService.continueTransactionProcessing(event);

        } catch (Exception e) {
            log.error("Error while consuming the fraud.check.completed message", e);

        }
    }
}
