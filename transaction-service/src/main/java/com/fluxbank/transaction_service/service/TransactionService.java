package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.controller.dto.*;
import com.fluxbank.transaction_service.messaging.producer.TransactionNotificationGenericProducer;
import com.fluxbank.transaction_service.messaging.producer.TransactionCompletedProducer;
import com.fluxbank.transaction_service.messaging.producer.TransactionFailedProducer;
import com.fluxbank.transaction_service.messaging.producer.TransactionInitiatedProducer;
import com.fluxbank.transaction_service.model.enums.TransactionDirection;
import com.fluxbank.transaction_service.model.events.FraudCheckResponseEvent;
import com.fluxbank.transaction_service.model.PixTransaction;
import com.fluxbank.transaction_service.model.Transaction;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import com.fluxbank.transaction_service.model.events.TransactionEvent;
import com.fluxbank.transaction_service.model.exceptions.InvalidTransactionException;
import com.fluxbank.transaction_service.model.exceptions.InvalidTransactionHistoryPageException;
import com.fluxbank.transaction_service.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    private final TransactionNotificationMapper notificationMapper;

    public TransactionService(TransactionEventService eventService, TransactionRepository repository, UserClientService userClientService, WalletClientService walletClientService, TransactionInitiatedProducer initiatedProducer, TransactionCompletedProducer completedProducer, TransactionFailedProducer failedProducer, TransactionNotificationMapper notificationMapper) {
        this.eventService = eventService;
        this.repository = repository;
        this.userClientService = userClientService;
        this.walletClientService = walletClientService;
        this.initiatedProducer = initiatedProducer;
        this.completedProducer = completedProducer;
        this.failedProducer = failedProducer;
        this.notificationMapper = notificationMapper;
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

        TransactionEvent event = eventService.createTransactionEvent(transactionPersisted);

        initiatedProducer.publish(event);


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

    public GetTransactionHistoryResponse getUserTransactionHistory(String userId, int start, int end) {
        if(start < 0 || end < 0) {
            throw new InvalidTransactionHistoryPageException();
        }


        UUID properlyUserId = UUID.fromString(userId);

        Page<Transaction> transactions = repository
                .findAllUserTransactions(properlyUserId, PageRequest.of(start,end, Sort.by("createdAt").descending()));

        return new GetTransactionHistoryResponse(
                transactions.map(t -> new TransactionInfoDto(
                        t.getAmount(),
                        t.getCurrency(),
                        t.getStatus(),
                        t.getPayeeId().equals(properlyUserId) ? TransactionDirection.RECEIVED : TransactionDirection.SENT,
                        t.getDescription(),
                        t.getTransactionType(),
                        t.getProcessedAt()
                )).toList()
        );
    }

    private WalletOperationResult performWalletOperations(Transaction transaction) {
        WithDrawRequest withDrawrequest = new WithDrawRequest(
                transaction.getPayerId().toString(),
                transaction.getAmount(),
                transaction.getId(),
                transaction.getTransactionType(),
                "Metadados",
                transaction.getCurrency()
        );

        WithDrawResponse withDrawResponse = null;
        DepositInWalletResponse depositResponse = null;

        try {
            withDrawResponse = walletClientService.withDrawWallet(withDrawrequest, transaction.getPayerId());

            DepositInWalletRequest depositRequest = new DepositInWalletRequest(
                    transaction.getId().toString(),
                    transaction.getAmount(),
                    transaction.getPayeeId().toString(),
                    transaction.getTransactionType().toString(),
                    "Metadados",
                    transaction.getDescription(),
                    transaction.getCurrency()
            );

            depositResponse = walletClientService.depositWallet(depositRequest);

            return new WalletOperationResult(withDrawResponse, depositResponse);

        } catch (Exception e) {
            return new WalletOperationResult(withDrawResponse, e.getMessage());
        }
    }



    private boolean isFraudDetected(FraudCheckResponseEvent event) {
        return "FRAUD_DETECTED".equals(event.getType());
    }

    private void handleFraudFailure(Transaction tx) {
        updateStatusAndPublish(tx, TransactionStatus.FAILED, failedProducer);
    }

    private void handleSuccessfulFraud(Transaction transaction) {
        WalletOperationResult result = performWalletOperations(transaction);

        if (result.isSuccess()) {
            updateStatusAndPublish(transaction, TransactionStatus.COMPLETED, completedProducer);
        } else {
            updateStatusAndPublish(transaction, TransactionStatus.FAILED, failedProducer);
            if (result.getWalletTransactionId() != null) {
                rollbackWithdraw(result.getWalletTransactionId(), transaction.getPayeeId().toString());
            }

            log.error("Error while requesting wallet service (transactionId={}): {}",
                    transaction.getId(), result.getErrorMessage());
        }
    }

    private void rollbackWithdraw(String walletTransactionId, String payeeId) {
        try {
            RefundWalletTransactionRequest refundRequest = new RefundWalletTransactionRequest(
                walletTransactionId,
                payeeId
            );
            walletClientService.refundWallets(refundRequest);
            log.info("Refund processed for wallet_transaction {}", walletTransactionId);
        } catch (Exception e) {
            log.error("Failed to refund wallet_transaction {}: {}", walletTransactionId, e.getMessage());
            // TODO: Implementar funcao de adicionar na DLQ
        }
    }

    private void updateStatusAndPublish(Transaction transaction, TransactionStatus status, TransactionNotificationGenericProducer transactionGenericProducer) {
        transaction.setStatus(status);
        transaction.setProcessedAt(LocalDateTime.now());

        eventService.createTransactionEvent(transaction);

        List<TransactionNotificationDto> notifications = notificationMapper.mapTransactionToNotifications(transaction);

        for (TransactionNotificationDto notification : notifications) {
            log.info("Publishing {} notification for transaction {} to user {}", 
                notification.eventType(), 
                notification.transactionId(), 
                notificationMapper.getNotificationTargetUserId(transaction, notification.eventType()));
                
            transactionGenericProducer.publish(notification);
        }

        repository.save(transaction);
        
        log.info("Transaction {} status updated to {} and {} notifications published", 
            transaction.getId(), status, notifications.size());
    }

}
