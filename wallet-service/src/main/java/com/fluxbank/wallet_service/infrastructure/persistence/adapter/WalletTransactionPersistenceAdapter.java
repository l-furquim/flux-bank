package com.fluxbank.wallet_service.infrastructure.persistence.adapter;

import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.TransactionType;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletTransactionEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.mapper.WalletTransactionMapper;
import com.fluxbank.wallet_service.infrastructure.persistence.repository.WalletJpaRepository;
import com.fluxbank.wallet_service.infrastructure.persistence.repository.WalletTransactionJpaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class WalletTransactionPersistenceAdapter {
    private final WalletTransactionJpaRepository repository;
    private final WalletTransactionMapper mapper;

    public WalletTransactionPersistenceAdapter(WalletTransactionJpaRepository repository, WalletTransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public WalletTransaction save(WalletTransaction walletTransaction, Wallet wallet){
        WalletEntity walletEntity = new WalletEntity();

        walletEntity.setId(wallet.getId());

        WalletTransactionEntity walletTransactionEntity = mapper.toEntity(walletTransaction, walletEntity);

        log.info("Transacao: {}", walletTransactionEntity);

        WalletTransaction transactionPersisted = mapper.toDomain(repository.save(walletTransactionEntity));

        repository.flush();

        return transactionPersisted;
    }

    @Transactional
    public void updateWalletTransactionStatus(UUID walletTransactionId, TransactionStatus status) {
        repository.updateStatus(status, walletTransactionId);
    }

    public List<WalletTransaction> findFilteredTransactions(UUID walletId, List<TransactionType> types, LocalDateTime start){
        List<WalletTransactionEntity> entities = repository
                .findFilteredTransactions(walletId, types, start);

        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    public WalletTransaction findById(UUID id) {
        Optional<WalletTransactionEntity> transaction = repository.findById(id);

        return transaction.map(mapper::toDomain).orElse(null);
    }

}
