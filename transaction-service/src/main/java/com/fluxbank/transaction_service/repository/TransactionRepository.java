package com.fluxbank.transaction_service.repository;

import com.fluxbank.transaction_service.model.Transaction;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

//    List<Transaction> findByStatus(TransactionStatus status);
//    List<Transaction> findByUserId(Long userId);
//    List<Transaction> findByOriginBill(String originBill);
//    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
//    List<Transaction> findByUserIdAndStatus(Long userId, TransactionStatus status);
//
//    @Query("SELECT t FROM Transaction t WHERE t.type = :transactionType")
//    List<Transaction> findByTransactionType(@Param("transactionType") Class<? extends Transaction> transactionType);
}
