package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.event.TransactionEvent;
import com.fluxbank.transaction_service.model.Transaction;
import com.fluxbank.transaction_service.repository.TransactionEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Service
public class TransactionEventService {

    private final TransactionEventRepository repository;

    public TransactionEventService(TransactionEventRepository repository) {
        this.repository = repository;
    }

    public void createTransactionEvent(Transaction transaction, String eventType) {

        long processingMs = Duration.between(LocalDateTime.now(), transaction.getCreatedAt()).toMillis();

        TransactionEvent event = TransactionEvent.builder()
                .amount(transaction.getAmount())
                .type(eventType)
                .transactionId(transaction.getId())
                .status(transaction.getStatus().toString())
                .currency(transaction.getCurrency().toString())
                .sourceService("transactionEventService")
                .processingDurationMs(processingMs)
                .timestamp(Instant.now())
                .build();

        log.info("Transaction event created for type: {}. Event: {}", eventType, event);


        repository.save(event);
    }

}
