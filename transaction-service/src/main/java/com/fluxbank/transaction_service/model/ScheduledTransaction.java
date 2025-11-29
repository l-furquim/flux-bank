package com.fluxbank.transaction_service.model;

import com.fluxbank.transaction_service.model.enums.Currency;
import com.fluxbank.transaction_service.model.enums.ScheduleRecurrence;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import com.fluxbank.transaction_service.model.enums.TransactionType;
import com.fluxbank.transaction_service.model.exceptions.InvalidTransactionException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@DiscriminatorValue("SCHEDULED_PIX")
public class ScheduledTransaction extends Transaction {

    public ScheduledTransaction() {
    }

    public ScheduledTransaction(
            Currency currency,
            String description,
            TransactionStatus status,
            BigDecimal amount,
            UUID payerId,
            UUID payeeId,
            String pixKey,
            LocalDate scheduledDate,
            LocalDateTime scheduledTime,
            ScheduleRecurrence recurrence
    ) {
        super(currency, description, status, amount, payerId, payeeId);
        this.pixKey = pixKey;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.recurrence = recurrence;
    }

    private String pixKey;

    private LocalDate scheduledDate;

    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private ScheduleRecurrence recurrence;

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.SCHEDULED_PIX;
    }

    @Override
    public void validateTransaction() throws InvalidTransactionException {

        if (this.getAmount() == null || this.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Invalid amount for the scheduled transaction.");
        }
        if (this.pixKey == null || this.pixKey.isEmpty()) {
            throw new InvalidTransactionException("Pix key cannot be null or empty for the scheduled transaction.");
        }
        if (this.scheduledDate == null) {
            throw new InvalidTransactionException("Scheduled date cannot be null for the scheduled transaction.");
        }
        if (this.scheduledTime == null) {
            throw new InvalidTransactionException("Scheduled time cannot be null for the scheduled transaction.");
        }
        if (this.recurrence == null) {
            throw new InvalidTransactionException("Recurrence cannot be null for the scheduled transaction.");
        }
        if(scheduledDate.isBefore(LocalDate.now())) {
            throw new InvalidTransactionException("Scheduled date cannot be in the past.");
        }

    }
}
