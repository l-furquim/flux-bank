package com.fluxbank.transaction_service.repository;

import com.fluxbank.transaction_service.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.payerId = :userId OR t.payeeId = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findAllUserTransactions(@Param("userId") UUID userId, Pageable pageable);
}
