package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.CreateWalletTransactionDto;
import com.fluxbank.wallet_service.application.port.WalletTransactionPort;
import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.InvalidDepositException;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletTransactionPersistenceAdapter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
                .createdAt(LocalDateTime.now())
                .transactionType(data.transactionType())
                .wallet(data.wallet())
                .status(TransactionStatus.PROCESSING)
                .description(data.description())
                .balanceAfter(balanceAfter)
                .balanceBefore(balanceBefore)
                .amount(data.amount())
                .build();

        UUID transactionId = persistenceAdapter.save(walletTransaction, data.wallet());

        if(data.wallet().getWalletStatus().equals(WalletStatus.BLOCKED) || data.wallet().getWalletStatus().equals(WalletStatus.CLOSED)) {
            throw new InvalidDepositException(transactionId, "Wallet is blocked from receiving deposits");
        }

        if(data.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositException(transactionId, "Invalid amount for a deposit");
        }

        return walletTransaction;
    }
}
