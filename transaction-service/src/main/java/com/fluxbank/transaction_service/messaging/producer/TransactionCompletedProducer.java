package com.fluxbank.transaction_service.messaging.producer;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.model.events.TransactionEvent;
import com.fluxbank.transaction_service.model.exceptions.TransactionEventSerializerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionCompletedProducer  extends Producer{

    @Value("${aws.sns.transaction-completed-topic}")
    private String topicArn;

    public TransactionCompletedProducer(AmazonSNS sns, ObjectMapper mapper) {
        super(sns, mapper);
    }

    @Override
    public void publish(TransactionEvent event) {
        try {
            log.info("Transaction completed confirmation topic is sending the event: {}", event);

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
