package com.fluxbank.transaction_service.messaging.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.event.TransactionEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionInitiatedConsumer {

    private final ObjectMapper mapper;

    public TransactionInitiatedConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @SqsListener("fraud")
    public void listen(String message){
        try {
            log.info("Event received: {}", message);

            JsonNode node = mapper.readTree(message);

            String messageJson = node.get("Message").asText();

            TransactionEvent event = mapper.readValue(messageJson, TransactionEvent.class);

            log.info("Transaction event received: {}", event);


        } catch (Exception e) {
            log.error("Error while consuming the transaction.initiated message: {}", e.getMessage());
        }
    }

}
