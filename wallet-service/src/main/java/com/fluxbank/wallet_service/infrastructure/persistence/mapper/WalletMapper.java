package com.fluxbank.wallet_service.infrastructure.persistence.mapper;

import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WalletMapper {

    public WalletEntity toEntity(Wallet wallet) {
        return WalletEntity.builder()
                .createdAt(wallet.getCreatedAt())
                .balance(wallet.getBalance())
                .blockedAmount(wallet.getBlockedAmount())
                .currency(wallet.getCurrency())
                .walletStatus(wallet.getWalletStatus())
                .userId(wallet.getUserId())
                .id(wallet.getId())
                .build();
    }

    public Wallet toDomain(WalletEntity wallet) {
        return Wallet.builder()
                .userId(wallet.getUserId())
                .currency(wallet.getCurrency())
                .walletStatus(wallet.getWalletStatus())
                .balance(wallet.getBalance())
                .blockedAmount(wallet.getBlockedAmount())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .id(wallet.getId())
                .build();
    }

}
