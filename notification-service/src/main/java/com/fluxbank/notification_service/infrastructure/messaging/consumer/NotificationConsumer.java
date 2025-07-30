package com.fluxbank.notification_service.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.notification_service.application.usecase.SendNotificationUsecase;
import com.fluxbank.notification_service.application.service.SnsMessageExtractor;
import com.fluxbank.notification_service.interfaces.dto.ExtractedMessage;
import com.fluxbank.notification_service.interfaces.dto.PixkeyCreatedEventData;
import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    private final ObjectMapper mapper;
    private final SendNotificationUsecase usecase;
    private final SnsMessageExtractor messageExtractor;

    public NotificationConsumer(ObjectMapper mapper, SendNotificationUsecase usecase, SnsMessageExtractor messageExtractor) {
        this.mapper = mapper;
        this.usecase = usecase;
        this.messageExtractor = messageExtractor;
    }

    @SqsListener("notification")
    public void listen(String message) {
        try {
            log.info("Raw SQS message received: {}", message);

            ExtractedMessage extractedMessage = messageExtractor.extractSnsMessage(message);
            
            log.info("Extracted message from source: {} topic: {}", 
                extractedMessage.eventSource(), extractedMessage.topicArn());

            switch (extractedMessage.eventSource()) {
                case TRANSACTION_SERVICE ->  {
                    TransactionNotificationEvent event = mapper.readValue(
                            extractedMessage.messageContent(),
                            TransactionNotificationEvent.class
                    );

                    log.info("Notification event parsed: {} from {}", event.eventType(), extractedMessage.eventSource());

                    usecase.sendTransactionUsecase(event);
                }
                case USER_SERVICE -> {
                    PixkeyCreatedEventData event = mapper.readValue(
                            extractedMessage.messageContent(),
                            PixkeyCreatedEventData.class
                    );

                    log.info("Notification event parsed from {} for pix key created", extractedMessage.eventSource());

                    usecase.sendPixkeyCreatedUsecase(event);
                }
            }



            log.info("Notification event processed successfully: {}", extractedMessage.eventSource());

        } catch (Exception e) {
            log.error("Error while processing notification message: {}", e.getMessage(), e);
            // TODO: Implementar envio para DLQ se necessário
        }
    }


}
