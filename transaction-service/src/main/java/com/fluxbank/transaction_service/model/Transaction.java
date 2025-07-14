package com.fluxbank.transaction_service.model;

import com.fluxbank.transaction_service.model.enums.Currency;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import com.fluxbank.transaction_service.model.enums.TransactionType;
import com.fluxbank.transaction_service.model.exceptions.InvalidTransactionException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Entity
@Table(name = "transactions")
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(precision = 10, scale = 2)
    @NotNull
    private BigDecimal amount;

    @NotNull
    private String externalIdentifier;

    @NotNull
    private String originBill;

    @NotNull
    private String destineBill;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;


    public abstract TransactionType getTransactionType();
    public abstract void validateTransaction() throws InvalidTransactionException;

}
