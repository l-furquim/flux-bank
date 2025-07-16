package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.model.events.TransactionEvent;
import com.fluxbank.transaction_service.messaging.producer.TransactionInitiatedProducer;
import com.fluxbank.transaction_service.model.Transaction;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import com.fluxbank.transaction_service.model.enums.TransactionType;
import com.fluxbank.transaction_service.repository.TransactionEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class TransactionEventService {

    private final TransactionEventRepository repository;
    private final TransactionInitiatedProducer initiatedProducer;

    public TransactionEventService(TransactionEventRepository repository, TransactionInitiatedProducer initiatedProducer) {
        this.repository = repository;
        this.initiatedProducer = initiatedProducer;
    }

    public void createTransactionEvent(Transaction transaction) {
        TransactionEvent event = TransactionEvent.builder()
                .id(UUID.randomUUID())
                .transactionId(transaction.getId())
                .currency(transaction.getCurrency().toString())
                .sourceService("transactionService")
                .processingDurationMs(null)
                .type(transaction.getTransactionType().toString())
                .status(TransactionStatus.INITIATED.toString())
                .amount(transaction.getAmount())
                .transactionType(TransactionType.PIX.toString())
                .timestamp(Instant.now())
                .build();

        log.info("Transaction event created for type: {}. Event: {}", event.getType(), event);

        initiatedProducer.publish(event);

        repository.save(event);
    }

}
