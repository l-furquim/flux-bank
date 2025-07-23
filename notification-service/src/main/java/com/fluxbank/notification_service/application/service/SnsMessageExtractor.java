package com.fluxbank.notification_service.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.notification_service.domain.enums.EventSource;
import com.fluxbank.notification_service.interfaces.dto.ExtractedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SnsMessageExtractor {
    
    private final ObjectMapper objectMapper;
    
    public SnsMessageExtractor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public ExtractedMessage extractSnsMessage(String rawMessage) {
        try {
            JsonNode snsMessage = objectMapper.readTree(rawMessage);
            
            String topicArn = snsMessage.path("TopicArn").asText();
            String timestamp = snsMessage.path("Timestamp").asText();
            String messageId = snsMessage.path("MessageId").asText();
            String actualMessage = snsMessage.path("Message").asText();
            
            EventSource eventSource = EventSource.fromTopicArn(topicArn);
            
            log.info("Extracted SNS message - Topic: {}, Source: {}, MessageId: {}", 
                topicArn, eventSource, messageId);
            
            return new ExtractedMessage(actualMessage, eventSource, topicArn, messageId, timestamp);
            
        } catch (Exception e) {
            log.error("Failed to extract SNS message envelope", e);
            return new ExtractedMessage(rawMessage, EventSource.SYSTEM, null, null, null);
        }
    }
}