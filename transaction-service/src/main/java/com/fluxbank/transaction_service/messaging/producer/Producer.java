package com.fluxbank.transaction_service.messaging.producer;

import com.amazonaws.services.sns.AmazonSNS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.transaction_service.model.events.TransactionEvent;

public abstract class Producer {

    protected final AmazonSNS sns;
    protected final ObjectMapper mapper;

    public Producer(AmazonSNS sns, ObjectMapper mapper) {
        this.sns = sns;
        this.mapper = mapper;
    }

    public abstract void publish(TransactionEvent event);

}
