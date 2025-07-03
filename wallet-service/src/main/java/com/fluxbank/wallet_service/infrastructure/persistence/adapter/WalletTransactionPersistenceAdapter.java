package com.fluxbank.wallet_service.infrastructure.persistence.adapter;

import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletTransactionEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.mapper.WalletTransactionMapper;
import com.fluxbank.wallet_service.infrastructure.persistence.repository.WalletTransactionJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WalletTransactionPersistenceAdapter {

    private final WalletTransactionJpaRepository repository;
    private final WalletTransactionMapper mapper;

    public WalletTransactionPersistenceAdapter(WalletTransactionJpaRepository repository, WalletTransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public UUID save(WalletTransaction walletTransaction, Wallet wallet){
        WalletTransactionEntity walletTransactionEntity = mapper.toEntity(walletTransaction, wallet);

        return mapper.toDomain(repository.save(walletTransactionEntity)).getId();
    }

    @Transactional
    public void updateWalletTransactionStatus(UUID walletTransactionId, TransactionStatus status) {
        repository.updateStatus(status, walletTransactionId);
    }


}
