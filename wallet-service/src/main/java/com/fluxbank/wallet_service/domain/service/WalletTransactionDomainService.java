package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.CreateWalletTransactionDto;
import com.fluxbank.wallet_service.application.port.WalletTransactionPort;
import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.TransactionType;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.InvalidDepositException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletTransactionPersistenceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class WalletTransactionDomainService implements WalletTransactionPort {

    private final WalletTransactionPersistenceAdapter persistenceAdapter;

    public WalletTransactionDomainService(WalletTransactionPersistenceAdapter persistenceAdapter) {
        this.persistenceAdapter = persistenceAdapter;
    }

    @Override
    public WalletTransaction create(CreateWalletTransactionDto data) {

        BigDecimal balanceBefore = data.wallet().getBalance();

        BigDecimal balanceAfter = balanceBefore.add(data.amount());

        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionType(data.transactionType())
                .wallet(data.wallet())
                .transactionId(data.transactionId())
                .metadata(data.metadata())
                .status(TransactionStatus.COMPLETED)
                .description(data.description())
                .balanceAfter(balanceAfter)
                .balanceBefore(balanceBefore)
                .amount(data.amount())
                .build();

        if(data.status().isPresent()){
            walletTransaction.setStatus(data.status().get());
        }

        WalletTransaction transaction = persistenceAdapter.save(walletTransaction, data.wallet());

        log.info("Transação criada: {}", transaction);

        if(data.wallet().getWalletStatus().equals(WalletStatus.BLOCKED) || data.wallet().getWalletStatus().equals(WalletStatus.CLOSED)) {
            throw new InvalidDepositException(transaction.getId(), "Wallet is blocked from receiving deposits");
        }

        if(data.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositException(transaction.getId(), "Invalid amount for a deposit");
        }

        return transaction;
    }

    @Override
    public WalletTransaction findById(UUID id) {
        return this.persistenceAdapter.findById(id);
    }

    @Override
    public void createRefundTransaction(WalletTransaction transactionToBeRefunded, Wallet payeeWallet) {
        BigDecimal amount = transactionToBeRefunded.getAmount();

        BigDecimal payerBalanceBefore = transactionToBeRefunded.getWallet().getBalance();
        BigDecimal payerBalanceAfter = payerBalanceBefore.add(amount);

        BigDecimal payeeBalanceBefore = payeeWallet.getBalance();
        BigDecimal payeeBalanceAfter = payeeBalanceBefore.subtract(amount);

        WalletTransaction payerRefundTransaction = WalletTransaction.builder()
                .transactionId(UUID.randomUUID())
                .amount(amount)
                .balanceBefore(payerBalanceBefore)
                .balanceAfter(payerBalanceAfter)
                .description("Refund: " + transactionToBeRefunded.getDescription())
                .metadata(transactionToBeRefunded.getMetadata())
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.REFUND)
                .wallet(transactionToBeRefunded.getWallet())
                .build();

        WalletTransaction payeeRefundTransaction = WalletTransaction.builder()
                .transactionId(UUID.randomUUID())
                .amount(amount.negate())
                .balanceBefore(payeeBalanceBefore)
                .balanceAfter(payeeBalanceAfter)
                .description("Refund debit: " + transactionToBeRefunded.getDescription())
                .metadata(transactionToBeRefunded.getMetadata())
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.REFUND)
                .wallet(payeeWallet)
                .build();

        persistenceAdapter.save(payerRefundTransaction, transactionToBeRefunded.getWallet());
        persistenceAdapter.save(payeeRefundTransaction, payeeWallet);

    }

    public List<WalletTransaction> getWalletTransactionsByTypesAndWallet(UUID walletId, List<TransactionType> types, LocalDateTime start){
        return persistenceAdapter.findFilteredTransactions(walletId, types, start);
    }
}
