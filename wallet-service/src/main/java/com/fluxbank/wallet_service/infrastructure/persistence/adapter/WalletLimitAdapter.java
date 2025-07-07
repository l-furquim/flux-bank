package com.fluxbank.wallet_service.infrastructure.persistence.adapter;

import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletLimitEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.mapper.WalletLimitMapper;
import com.fluxbank.wallet_service.infrastructure.persistence.mapper.WalletMapper;
import com.fluxbank.wallet_service.infrastructure.persistence.repository.WalletLimitJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class WalletLimitAdapter {

    private final WalletLimitJpaRepository repository;
    private final WalletLimitMapper mapper;
    private final WalletMapper walletMapper;

    public WalletLimitAdapter(WalletLimitJpaRepository repository, WalletLimitMapper mapper, WalletMapper walletMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.walletMapper = walletMapper;
    }

    public void create(WalletLimit walletLimit, WalletEntity wallet) {
        WalletLimitEntity limitToBePersisted = mapper.toEntity(walletLimit, wallet);

        repository.save(limitToBePersisted);

        repository.flush();
    }

    public List<WalletLimit> findByUserAndWalletId(UUID userId, UUID walletId) {
        List<WalletLimitEntity> limitsFounded = repository.findByUserAndWalletId(userId, walletId);

        return limitsFounded
                .stream()
                .map(l -> mapper.toDomain(l, walletMapper.toDomain(l.getWallet())))
                .toList();
    }

    @Transactional
    public void updateWalletLimit()
}
