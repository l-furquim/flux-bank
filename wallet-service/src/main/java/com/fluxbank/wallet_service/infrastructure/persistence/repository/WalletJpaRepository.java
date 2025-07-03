package com.fluxbank.wallet_service.infrastructure.persistence.repository;

import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface WalletJpaRepository extends JpaRepository<WalletEntity, UUID> {

    @Query("SELECT w FROM WalletEntity w WHERE w.userId = :userId")
    List<WalletEntity> findByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE WalletEntity w SET w.balance = :newBalance WHERE w.id = :id")
    void updateWalletBalance(@Param("newBalance") BigDecimal newBalance, @Param("id") UUID id);

}
