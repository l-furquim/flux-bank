package com.fluxbank.transaction_service.messaging.producer;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.model.events.TransactionEvent;
import com.fluxbank.transaction_service.model.exceptions.TransactionEventSerializerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransactionInitiatedProducer extends Producer {

    @Value("${aws.sns.transaction-initiated-topic}")
    private String topicArn;

    public TransactionInitiatedProducer(AmazonSNS sns, ObjectMapper mapper) {
        super(sns, mapper);
    }

    @Override
    public void publish(TransactionEvent event) {
        try {
            String message = super.mapper.writeValueAsString(event);

            PublishRequest request = new PublishRequest()
                    .withTopicArn(topicArn)
                    .withMessage(message);

            super.sns.publish(request);
        } catch (JsonProcessingException e) {
            throw new TransactionEventSerializerException("Error while serializing the message data: " + e.getMessage());
        }
    }

}
