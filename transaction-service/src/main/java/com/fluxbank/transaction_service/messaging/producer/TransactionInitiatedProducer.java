package com.fluxbank.transaction_service.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.model.Transaction;
import com.fluxbank.transaction_service.model.events.TransactionEvent;
import com.fluxbank.transaction_service.model.exceptions.TransactionEventSerializerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
public class TransactionInitiatedProducer {

    @Value("${aws.sns.transaction-initiated-topic}")
    private String topicArn;

    private final SnsClient sns;
    private final ObjectMapper mapper;

    public TransactionInitiatedProducer(SnsClient sns, ObjectMapper mapper) {
        this.sns = sns;
        this.mapper = mapper;
    }

    public void publish(TransactionEvent event) {
        try {
            String message = mapper.writeValueAsString(event);

            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();

            sns.publish(request);
        } catch (JsonProcessingException e) {
            throw new TransactionEventSerializerException("Error while serializing the message data: " + e.getMessage());
        }
    }

}
