package com.fluxbank.transaction_service.messaging.producer;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.event.TransactionEvent;
import com.fluxbank.transaction_service.model.exceptions.TransactionEventSerializerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransactionInitiatedProducer {

    private final AmazonSNS sns;
    private final ObjectMapper mapper;

    @Value("${aws.sqs.transaction-initiated-topic}")
    private String topicArn;

    public TransactionInitiatedProducer(AmazonSNS sns, ObjectMapper mapper) {
        this.sns = sns;
        this.mapper = mapper;
    }

    public void publish(TransactionEvent event) {
        try {
            String message = mapper.writeValueAsString(event);

            PublishRequest request = new PublishRequest()
                    .withTopicArn(topicArn)
                    .withMessage(message);

            sns.publish(request);
        } catch (JsonProcessingException e) {
            throw new TransactionEventSerializerException("Error while serializing the message data: " + e.getMessage());
        }
    }

}
