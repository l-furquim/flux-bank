package com.fluxbank.wallet_service.infrastructure.persistence.repository;

import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletLimitEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletLimitJpaRepository extends JpaRepository<WalletLimitEntity, UUID> {

    @Query("SELECT w FROM WalletLimitEntity w WHERE w.userId = :userId AND w.walletId = :walletId")
    List<WalletLimitEntity> findByUserAndWalletId(@Param("userId") UUID userId, @Param("walletId") UUID walletId);

}
