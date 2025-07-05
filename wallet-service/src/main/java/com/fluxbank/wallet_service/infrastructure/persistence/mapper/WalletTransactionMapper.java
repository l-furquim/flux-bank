package com.fluxbank.wallet_service.infrastructure.persistence.mapper;

import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletTransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletTransactionMapper {

    private final WalletMapper walletMapper;

    public WalletTransactionMapper(WalletMapper walletMapper) {
        this.walletMapper = walletMapper;
    }

    public WalletTransaction toDomain(WalletTransactionEntity walletTransactionEntity) {
        return WalletTransaction.builder()
                .id(walletTransactionEntity.getId())
                .wallet(walletMapper.toDomain(walletTransactionEntity.getWallet()))
                .createdAt(walletTransactionEntity.getCreatedAt())
                .balanceAfter(walletTransactionEntity.getBalanceAfter())
                .balanceBefore(walletTransactionEntity.getBalanceBefore())
                .transactionType(walletTransactionEntity.getTransactionType())
                .description(walletTransactionEntity.getDescription())
                .metadata(walletTransactionEntity.getMetadata())
                .status(walletTransactionEntity.getStatus())
                .amount(walletTransactionEntity.getAmount())
                .transactionId(walletTransactionEntity.getTransactionId())
                .build();
    }

    public WalletTransactionEntity toEntity(WalletTransaction walletTransaction, WalletEntity wallet) {
        return WalletTransactionEntity.builder()
                .wallet(wallet)
                .balanceAfter(walletTransaction.getBalanceAfter())
                .balanceBefore(walletTransaction.getBalanceBefore())
                .transactionType(walletTransaction.getTransactionType())
                .description(walletTransaction.getDescription())
                .metadata(walletTransaction.getMetadata())
                .status(walletTransaction.getStatus())
                .amount(walletTransaction.getAmount())
                .transactionId(walletTransaction.getTransactionId())
                .build();
    }


}
