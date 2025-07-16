package com.fluxbank.transaction_service.model;

import com.fluxbank.transaction_service.model.enums.Currency;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import com.fluxbank.transaction_service.model.enums.TransactionType;
import com.fluxbank.transaction_service.model.exceptions.InvalidTransactionException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@DiscriminatorValue("PIX")
public class PixTransaction extends Transaction {

    public PixTransaction() {
    }

    public PixTransaction(Currency currency, String description, TransactionStatus status, BigDecimal amount, UUID payerId, UUID payeeId, String key) {
        super(currency, description, status, amount, payerId, payeeId);
        this.key = key;
    }

    @NotNull
    private String key;

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.PIX;
    }

    @Override
    public void validateTransaction() throws InvalidTransactionException {
        if (this.key == null || this.key.trim().isEmpty()) {
            throw new InvalidTransactionException("Pix key is necessary.");
        }

        if (getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount need's to be greater than zero.");
        }

        validateKey();
    }

    private void validateKey() throws InvalidTransactionException{
        if(this.key.contains("@")){
            String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

            if(!emailRegex.matches(this.key)) throw new InvalidTransactionException("Invalid key.");
        }

        if(this.key.length() == 11){
            if(!isValidCPF(this.key) && !isValidTel(this.key)) {
                throw new InvalidTransactionException("Invalid key.");
            }
        }

        try{
            UUID.fromString(this.key);
        } catch (IllegalArgumentException e) {
            throw new InvalidTransactionException("Invalid key.");
        }
    }

    public boolean isValidTel(String tel) {
        return tel.startsWith("119");
    }

    public static boolean isValidCPF(String cpf) {
        if (cpf == null) return false;

        // Remove pontos e traço
        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }

            int firstCheckDigit = 11 - (sum % 11);
            if (firstCheckDigit >= 10) firstCheckDigit = 0;
            if (firstCheckDigit != (cpf.charAt(9) - '0')) return false;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }

            int secondCheckDigit = 11 - (sum % 11);
            if (secondCheckDigit >= 10) secondCheckDigit = 0;
            return secondCheckDigit == (cpf.charAt(10) - '0');
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
