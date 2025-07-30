package com.fluxbank.user_service.infrastructure.messasing.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.user_service.domain.service.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
@Component
public class PixkeyCreatedSqsProducerImpl implements MessagingService {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    public PixkeyCreatedSqsProducerImpl(SnsClient snsClient, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(Object message) {
        try {
            log.info("Transaction completed confirmation topic is sending the event: {}", event);

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
