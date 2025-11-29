package com.fluxbank.transaction_service.messaging.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.model.PixTransaction;
import com.fluxbank.transaction_service.service.TransactionService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledTransactionConsumer {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

     @SqsListener("scheduled-transactions")
     public void listen(String message){
        try {
            JsonNode node = objectMapper.readTree(message);

            String messageJson = node.get("Message").asText();

            PixTransaction event = objectMapper.readValue(messageJson, PixTransaction.class);

            transactionService.send();
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error while processing scheduled transactions: " + e.getMessage());
        }
     }

}
