package com.fluxbank.wallet_service.domain.models;

import com.fluxbank.wallet_service.domain.enums.MethodStatus;
import com.fluxbank.wallet_service.domain.enums.MethodType;
import com.fluxbank.wallet_service.domain.exception.ExpiredPaymentMethodException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class PaymentMethods {

    private Wallet wallet;

    private MethodType methodType;

    private String displayName;

    private String lastFourDigits;

    private LocalDateTime expiryDate;

    private MethodStatus status;

    private Boolean isDefault = false;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void activate() {
        if (isExpired()) {
            throw new ExpiredPaymentMethodException();
        }
        this.status = MethodStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }

    public boolean isUsable() {
        return status == MethodStatus.ACTIVE && !isExpired();
    }

}
