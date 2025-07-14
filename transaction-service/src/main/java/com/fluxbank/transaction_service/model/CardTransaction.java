package com.fluxbank.transaction_service.model;

import com.fluxbank.transaction_service.model.enums.CardType;
import com.fluxbank.transaction_service.model.enums.Currency;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import com.fluxbank.transaction_service.model.enums.TransactionType;
import com.fluxbank.transaction_service.model.exceptions.InvalidTransactionException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@Entity
@DiscriminatorValue("CARD")
public class CardTransaction extends Transaction {

    public CardTransaction() {
    }

    public CardTransaction(Currency currency, String description, TransactionStatus status, BigDecimal amount, String originBill, String destineBill, String lastFourDigits, String flag, String authCode, int installments, CardType cardType) {
        super(currency, description, status, amount, originBill, destineBill);
        this.lastFourDigits = lastFourDigits;
        this.flag = flag;
        this.authCode = authCode;
        this.installments = installments;
        this.cardType = cardType;
    }

    @NotNull
    private String lastFourDigits;

    @NotNull
    private String flag;

    @NotNull
    private String authCode;

    @NotNull
    private int installments;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Override
    public TransactionType getTransactionType() {
        return cardType.equals(CardType.CREDIT) ? TransactionType.CREDIT : TransactionType.DEBIT;
    }

    @Override
    public void validateTransaction() throws InvalidTransactionException {
        if (cardType.equals(CardType.CREDIT) && installments < 1) {
            throw new InvalidTransactionException("Installments number must be greater than zero.");
        }
    }
}
