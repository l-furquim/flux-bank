package com.fluxbank.wallet_service.domain.models;

import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal   amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(length = 255)
    private String externalReference;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    private LocalDateTime processedAt;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    public boolean isCompleted() {
        return status.equals(TransactionStatus.COMPLETED);
    }

    public boolean isCredit() {
        return transactionType.equals(TransactionType.CREDIT) ||
                transactionType.equals(TransactionType.REFUND);
    }

    public boolean isDebit() {
        return transactionType.equals(TransactionType.DEBIT);
    }

}
