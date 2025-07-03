package com.fluxbank.wallet_service.infrastructure.persistence.adapter;

import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletTransactionEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.mapper.WalletTransactionMapper;
import com.fluxbank.wallet_service.infrastructure.persistence.repository.WalletJpaRepository;
import com.fluxbank.wallet_service.infrastructure.persistence.repository.WalletTransactionJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WalletTransactionPersistenceAdapter {

    private final WalletJpaRepository walletRepository;
    private final WalletTransactionJpaRepository repository;
    private final WalletTransactionMapper mapper;

    public WalletTransactionPersistenceAdapter(WalletJpaRepository walletRepository, WalletTransactionJpaRepository repository, WalletTransactionMapper mapper) {
        this.walletRepository = walletRepository;
        this.repository = repository;
        this.mapper = mapper;
    }

    public WalletTransaction save(WalletTransaction walletTransaction, Wallet wallet){
        WalletEntity walletEntity = walletRepository.findById(wallet.getId()).get();
        WalletTransactionEntity walletTransactionEntity = mapper.toEntity(walletTransaction, walletEntity);

        return mapper.toDomain(repository.save(walletTransactionEntity));
    }

    @Transactional
    public void updateWalletTransactionStatus(UUID walletTransactionId, TransactionStatus status) {
        repository.updateStatus(status, walletTransactionId);
    }


}
