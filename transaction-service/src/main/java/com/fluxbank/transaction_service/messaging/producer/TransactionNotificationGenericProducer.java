package com.fluxbank.transaction_service.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.controller.dto.TransactionNotificationDto;
import software.amazon.awssdk.services.sns.SnsClient;

public abstract class TransactionNotificationGenericProducer {

    protected final SnsClient sns;
    protected final ObjectMapper mapper;

    public TransactionNotificationGenericProducer(SnsClient sns, ObjectMapper mapper) {
        this.sns = sns;
        this.mapper = mapper;
    }

    public abstract void publish(TransactionNotificationDto event);

}
