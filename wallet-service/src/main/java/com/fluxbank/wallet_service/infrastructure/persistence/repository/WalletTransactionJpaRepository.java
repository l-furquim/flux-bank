package com.fluxbank.wallet_service.infrastructure.persistence.repository;

import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletTransactionEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletTransactionJpaRepository extends JpaRepository<WalletTransactionEntity, UUID> {

    @Modifying
    @Query("UPDATE WalletTransactionEntity wt SET wt.status = :status WHERE id = :id")
    void updateStatus(@Param("status")TransactionStatus status, @Param("id") UUID id);

}
