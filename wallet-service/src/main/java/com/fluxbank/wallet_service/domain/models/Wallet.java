package com.fluxbank.wallet_service.domain.models;


import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class Wallet {

    private UUID id;

    private UUID userId;

    private BigDecimal balance;

    private Currency currency;

    private WalletStatus walletStatus;

    private BigDecimal blockedAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public boolean isAllowedToUse() {
        return this.walletStatus.equals(WalletStatus.ACTIVE);
    }

    public boolean isClosed() {
        return this.walletStatus.equals(WalletStatus.CLOSED);
    }

    public BigDecimal getAvailableBalance() {
        return balance.subtract(blockedAmount != null ? blockedAmount : BigDecimal.ZERO);
    }

    public boolean hasAvailableBalance(BigDecimal amount) {
        return getAvailableBalance().compareTo(amount) >= 0;
    }

    public void deposit(BigDecimal amount){
        this.balance = this.balance.add(amount);
    }

    public void withDraw(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

}
