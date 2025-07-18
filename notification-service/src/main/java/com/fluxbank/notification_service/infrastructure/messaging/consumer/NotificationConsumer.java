package com.fluxbank.notification_service.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.notification_service.application.usecase.SendNotificationUsecase;
import com.fluxbank.notification_service.domain.enums.NotificationType;
import com.fluxbank.notification_service.interfaces.dto.SendNotificationEventDto;
import com.fluxbank.notification_service.interfaces.dto.TransactionEventDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    private final ObjectMapper mapper;
    private final SendNotificationUsecase usecase;

    public NotificationConsumer(ObjectMapper mapper, SendNotificationUsecase usecase) {
        this.mapper = mapper;
        this.usecase = usecase;
    }

    @SqsListener("notification")
    public void listen(String message){
        try{
            log.info("Event received: {}", message);

            JsonNode node = mapper.readTree(message);

            String messageJson = node.get("Message").asText();

            TransactionEventDto event = mapper.readValue(messageJson, TransactionEventDto.class);

            log.info("Fraud check event received: {}", event);

            event.

            usecase.send(
                    new SendNotificationEventDto(
                            NotificationType.EMAIL,

                    )
            );

        } catch (Exception e) {

        }


    }


}
