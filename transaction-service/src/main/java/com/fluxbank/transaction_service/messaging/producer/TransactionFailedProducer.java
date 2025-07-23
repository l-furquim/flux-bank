package com.fluxbank.transaction_service.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.controller.dto.TransactionNotificationDto;
import com.fluxbank.transaction_service.model.exceptions.TransactionEventSerializerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
public class TransactionFailedProducer extends TransactionNotificationGenericProducer {

    @Value("${aws.sns.transaction-failed-topic}")
    private String topicArn;

    public TransactionFailedProducer(SnsClient sns, ObjectMapper mapper) {
        super(sns, mapper);
    }

    @Override
    public void publish(TransactionNotificationDto event) {
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
