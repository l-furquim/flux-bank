package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.controller.dto.SendPixRequest;
import com.fluxbank.transaction_service.controller.dto.SendPixResponse;
import com.fluxbank.transaction_service.model.PixTransaction;
import com.fluxbank.transaction_service.model.Transaction;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import com.fluxbank.transaction_service.model.exceptions.InvalidTransactionException;
import com.fluxbank.transaction_service.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class TransactionService {

    private final TransactionEventService eventService;
    private final TransactionRepository repository;

    public TransactionService(TransactionEventService eventService, TransactionRepository repository) {
        this.eventService = eventService;
        this.repository = repository;
    }

    public SendPixResponse sendPix(SendPixRequest request){
        if(request.amount().compareTo(BigDecimal.ZERO) >= 0) {
            throw new InvalidTransactionException("Invalid amount for the transaction.");
        }

        LocalDateTime issuedAt = LocalDateTime.now();

        PixTransaction transaction = new PixTransaction(
                request.currency(),
                request.description(),
                TransactionStatus.INITIATED,
                request.amount(),
                UUID.randomUUID().toString(), // nao implementado ainda
                UUID.randomUUID().toString(), // nao implementado ainda
                request.destineKey()
        );

        eventService.createTransactionEvent(transaction);

        Transaction transactionPersisted = repository.save(transaction);

        return new SendPixResponse(
                transactionPersisted.getId(),
                "Transaction processing initiated.",
                issuedAt
        );
    }

}
