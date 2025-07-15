package com.fluxbank.fraud_service.infrastructure.messaging.producer;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.fraud_service.domain.exceptions.FraudCheckedCompletedSerializerException;
import com.fluxbank.fraud_service.interfaces.dto.FraudAnalysisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FraudCheckCompletedProducer {

    private final AmazonSNS sns;
    private final ObjectMapper mapper;

    @Value("${aws.sns.fraud-check-completed-topic}")
    private String topicArn;

    public FraudCheckCompletedProducer(AmazonSNS sns, ObjectMapper mapper) {
        this.sns = sns;
        this.mapper = mapper;
    }

    public void publish(FraudAnalysisResponse event){
        try {
            String message = mapper.writeValueAsString(event);

            PublishRequest request = new PublishRequest()
                    .withTopicArn(topicArn)
                    .withMessage(message);

            sns.publish(request);
        } catch (JsonProcessingException e) {
            throw new FraudCheckedCompletedSerializerException("Error while serializing the message data: " + e.getMessage());
        }
    }

}
