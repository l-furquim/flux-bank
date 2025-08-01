package com.fluxbank.wallet_service.infrastructure.persistence.adapter;

import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.mapper.WalletMapper;
import com.fluxbank.wallet_service.infrastructure.persistence.repository.WalletJpaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class WalletPersistenceAdapter {

    private final WalletJpaRepository repository;
    private final WalletMapper mapper;

    public WalletPersistenceAdapter(WalletJpaRepository repository, WalletMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Wallet saveWallet(Wallet wallet){
        WalletEntity walletEntity = mapper.toEntity(wallet);

        log.debug("Saving wallet in db {}", wallet);

        Wallet walletWithId = mapper.toDomain(repository.save(walletEntity));

        repository.flush();

        return walletWithId;
    }

    @Transactional
    public void updateWalletBalance(BigDecimal balance, UUID id){
        this.repository.updateWalletBalance(balance, id);
    }

    public Wallet findWalletById(UUID walletId) {
        Optional<WalletEntity> walletEntity = repository.findById(walletId);

        return walletEntity.map(mapper::toDomain).orElse(null);
    }

    @Transactional
    public void updateWallet(Wallet wallet) {

        WalletEntity existingWallet = WalletEntity.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .blockedAmount(wallet.getBlockedAmount())
                .currency(wallet.getCurrency())
                .walletStatus(wallet.getWalletStatus())
                .userId(wallet.getUserId())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();

        repository.save(existingWallet);
    }

    @Transactional
    public void deleteWallet(Wallet wallet) {
        Optional<WalletEntity> walletToBeDeleted = repository.findById(wallet.getId());

        if(walletToBeDeleted.isEmpty()) return;

        repository.delete(walletToBeDeleted.get());
    }

    public List<Wallet> findWalletsByUserId(UUID userId) {
        List<WalletEntity> walletsFounded = repository.findByUserId(userId);

        log.debug("Wallets founded {}", walletsFounded);

         return walletsFounded.stream().map(mapper::toDomain
         ).toList();
    }

}
