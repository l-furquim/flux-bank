package com.fluxbank.wallet_service.infrastructure.persistence.repository;

import com.fluxbank.wallet_service.domain.enums.LimitStatus;
import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletLimitEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletLimitJpaRepository extends JpaRepository<WalletLimitEntity, UUID> {

    @Query("SELECT w FROM WalletLimitEntity w WHERE w.wallet.userId = :userId AND w.wallet.id = :walletId")
    List<WalletLimitEntity> findByUserAndWalletId(@Param("userId") UUID userId, @Param("walletId") UUID walletId);

    @Modifying
    @Query(
            "UPDATE WalletLimitEntity " +
            "w SET w.usedAmount = w.usedAmount + :amount, " +
            "w.status = :status " +
            "WHERE w.id = :id")
    void updateWalletLimit(@Param("id") UUID id, @Param("amount") BigDecimal amount, @Param("status") LimitStatus status);

    @Query("SELECT w from WalletLimitEntity w WHERE w.wallet.id = :walletId AND w.limitType = :type")
    Optional<WalletLimitEntity> findWalletLimitByTypeAndWalletId(@Param("walletId") UUID walletId, @Param("type") LimitType type);

    @Query("SELECT w FROM WalletLimitEntity w WHERE w.wallet.id = :walletId")
    List<WalletLimitEntity> findWalletLimitsByWalletId(@Param("walletId") UUID walletId);

}
