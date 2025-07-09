package com.fluxbank.wallet_service.infrastructure.persistence.mapper;

import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletLimitEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletLimitMapper {

    private final WalletMapper walletMapper;

    public WalletLimitMapper(WalletMapper walletMapper) {
        this.walletMapper = walletMapper;
    }

    public WalletLimit toDomain(WalletLimitEntity walletLimit) {
        return WalletLimit.builder()
                .id(walletLimit.getId())
                .wallet(walletMapper.toDomain(walletLimit.getWallet()))
                .status(walletLimit.getStatus())
                .limitAmount(walletLimit.getLimitAmount())
                .limitType(walletLimit.getLimitType())
                .resetDate(walletLimit.getResetDate())
                .usedAmount(walletLimit.getUsedAmount())
                .createdAt(walletLimit.getCreatedAt())
                .updatedAt(walletLimit.getUpdatedAt())
                .build();
    }

    public WalletLimitEntity toEntity(WalletLimit walletLimit, WalletEntity wallet) {
        return WalletLimitEntity.builder()
                .wallet(wallet)
                .status(walletLimit.getStatus())
                .limitAmount(walletLimit.getLimitAmount())
                .limitType(walletLimit.getLimitType())
                .resetDate(walletLimit.getResetDate())
                .usedAmount(walletLimit.getUsedAmount())
                .build();
    }

}
