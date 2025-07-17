package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.controller.dto.*;
import com.fluxbank.transaction_service.messaging.producer.Producer;
import com.fluxbank.transaction_service.messaging.producer.TransactionCompletedProducer;
import com.fluxbank.transaction_service.messaging.producer.TransactionFailedProducer;
import com.fluxbank.transaction_service.messaging.producer.TransactionInitiatedProducer;
import com.fluxbank.transaction_service.model.events.FraudCheckResponseEvent;
import com.fluxbank.transaction_service.model.PixTransaction;
import com.fluxbank.transaction_service.model.Transaction;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import com.fluxbank.transaction_service.model.events.TransactionEvent;
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
    private final UserClientService userClientService;
    private final WalletClientService walletClientService;
    private final TransactionInitiatedProducer initiatedProducer;
    private final TransactionCompletedProducer completedProducer;
    private final TransactionFailedProducer failedProducer;

    public TransactionService(TransactionEventService eventService, TransactionRepository repository, UserClientService userClientService, WalletClientService walletClientService, TransactionInitiatedProducer initiatedProducer, TransactionCompletedProducer completedProducer, TransactionFailedProducer failedProducer) {
        this.eventService = eventService;
        this.repository = repository;
        this.userClientService = userClientService;
        this.walletClientService = walletClientService;
        this.initiatedProducer = initiatedProducer;
        this.completedProducer = completedProducer;
        this.failedProducer = failedProducer;
    }

    public SendPixResponse sendPix(SendPixRequest request, String userId){
        if(request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Invalid amount for the transaction.");
        }

        UUID payeeResolvedId = userClientService.resolvePixKey(request.destineKey());

        LocalDateTime issuedAt = LocalDateTime.now();

        PixTransaction transaction = new PixTransaction(
                request.currency(),
                request.description(),
                TransactionStatus.INITIATED,
                request.amount(),
                UUID.fromString(userId),
                payeeResolvedId,
                request.destineKey()
        );
        Transaction transactionPersisted = repository.save(transaction);

        TransactionEvent eventCreated = eventService.createTransactionEvent(transactionPersisted);

        initiatedProducer.publish(eventCreated); 


        return new SendPixResponse(
                transactionPersisted.getId(),
                "Transaction processing initiated.",
                issuedAt
        );
    }

    public void continueTransactionProcessing(FraudCheckResponseEvent event) {
        log.info("Transaction id: {}", event.getTransactionId());

        repository.findById(UUID.fromString(event.getTransactionId()))
                .ifPresentOrElse(transaction -> {
                    if (isFraudDetected(event)) {
                        handleFraudFailure(transaction);
                    } else {
                        handleSuccessfulFraud(transaction);
                    }
                },
                () -> log.warn("Transaction not found: {}", event.getTransactionId())
                );
    }

    private void performWalletOperations(Transaction transaction) {
        WithDrawRequest withDrawrequest = new WithDrawRequest(
                transaction.getPayerId().toString(),
                transaction.getAmount(),
                transaction.getId(),
                transaction.getTransactionType(),
                "Metadados",
                transaction.getCurrency()
        );


        DepositInWalletRequest depositRequest = new DepositInWalletRequest(
                transaction.getId().toString(),
                transaction.getAmount(),
                transaction.getPayeeId().toString(),
                transaction.getTransactionType().toString(),
                "Metadados",
                transaction.getDescription(),
                transaction.getCurrency()
            );

        walletClientService.withDrawWallet(withDrawrequest, transaction.getPayerId());

        walletClientService.depositWallet(depositRequest);
    }


    private boolean isFraudDetected(FraudCheckResponseEvent event) {
        return "FRAUD_DETECTED".equals(event.getType());
    }

    private void handleFraudFailure(Transaction tx) {
        updateStatusAndPublish(tx, TransactionStatus.FAILED, failedProducer);
    }

    private void handleSuccessfulFraud(Transaction transaction) {
        try{
            performWalletOperations(transaction);

            updateStatusAndPublish(transaction, TransactionStatus.COMPLETED, completedProducer);
        } catch (Exception e) {
            updateStatusAndPublish(transaction, TransactionStatus.FAILED, failedProducer);
            log.error("Error while requesting wallet service (transactionId={}): {}", transaction.getId(), e.getMessage());
        }
    }

    private void updateStatusAndPublish(Transaction transaction, TransactionStatus status, Producer producer) {
        transaction.setStatus(status);
        transaction.setProcessedAt(LocalDateTime.now());

        TransactionEvent evt = eventService.createTransactionEvent(transaction);

        producer.publish(evt);

        repository.save(transaction);
    }

}
