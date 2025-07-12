package com.fluxbank.wallet_service.infrastructure.persistence.adapter;

import com.fluxbank.wallet_service.domain.enums.LimitStatus;
import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletLimitEntity;
import com.fluxbank.wallet_service.infrastructure.persistence.mapper.WalletLimitMapper;
import com.fluxbank.wallet_service.infrastructure.persistence.repository.WalletLimitJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WalletLimitAdapter {

    private final WalletLimitJpaRepository repository;
    private final WalletLimitMapper mapper;

    public WalletLimitAdapter(WalletLimitJpaRepository repository, WalletLimitMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public void create(WalletLimit walletLimit, WalletEntity wallet) {
        WalletLimitEntity limitToBePersisted = mapper.toEntity(walletLimit, wallet);

        repository.save(limitToBePersisted);

        repository.flush();
    }

    @Transactional
    public void updateWalletLimit(UUID walletLimitId, BigDecimal amount, LimitStatus status){
        repository.updateWalletLimit(walletLimitId, amount, status);
    }

    public Optional<WalletLimit> findWalletLimitByTypeAndWalletId(LimitType type, Wallet wallet) {
        Optional<WalletLimitEntity> walletLimit = repository.findWalletLimitByTypeAndWalletId(wallet.getId(), type);

        return walletLimit.map(mapper::toDomain);
    }

    public Optional<WalletLimit> findById(UUID limitId){
        Optional<WalletLimitEntity> limit = repository.findById(limitId);

        return limit.map(mapper::toDomain);
    }

    public List<WalletLimit> findByWalletId(UUID walletId){
        List<WalletLimitEntity> limits = repository.findWalletLimitsByWalletId(walletId);

        return limits.stream().map(mapper::toDomain).toList();
    }

    @Transactional
    public void resetDailyLimits(){
        this.repository.resetDailyLimits();
    }

    @Transactional
    public void resetMonthlyLimits(){
        this.repository.resetMonthlyLimits();
    }



}
