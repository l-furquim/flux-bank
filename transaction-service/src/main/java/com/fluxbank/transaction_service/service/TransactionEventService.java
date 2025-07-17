package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.model.events.TransactionEvent;
import com.fluxbank.transaction_service.model.Transaction;

import com.fluxbank.transaction_service.repository.TransactionEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class TransactionEventService {

    private final TransactionEventRepository repository;

    public TransactionEventService(TransactionEventRepository repository) {
        this.repository = repository;
    }

    public TransactionEvent createTransactionEvent(Transaction transaction) {
        long msDuration = Duration.between(LocalDateTime.now(), transaction.getCreatedAt()).toMillis();

        TransactionEvent event = TransactionEvent.builder()
                .id(UUID.randomUUID())
                .transactionId(transaction.getId())
                .currency(transaction.getCurrency().toString())
                .sourceService("transactionService")
                .processingDurationMs(msDuration)
                .type(transaction.getTransactionType().toString())
                .status(transaction.getStatus().toString())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType().toString())
                .timestamp(Instant.now())
                .build();

        log.info("Transaction event created for type: {}. Event: {}", event.getType(), event);

        repository.save(event);

        return event;
    }

}
