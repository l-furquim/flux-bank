package com.fluxbank.transaction_service.controller.dto;

import lombok.Getter;

@Getter
public class WalletOperationResult {
    private final WithDrawResponse withdrawResponse;
    private final DepositInWalletResponse depositResponse;
    private final boolean success;
    private final String errorMessage;

    public WalletOperationResult(WithDrawResponse withdrawResponse, DepositInWalletResponse depositResponse) {
        this.withdrawResponse = withdrawResponse;
        this.depositResponse = depositResponse;
        this.success = true;
        this.errorMessage = null;
    }

    public WalletOperationResult(WithDrawResponse withdrawResponse, String errorMessage) {
        this.withdrawResponse = withdrawResponse;
        this.depositResponse = null;
        this.success = false;
        this.errorMessage = errorMessage;
    }

    public String getWalletTransactionId() {
        return withdrawResponse != null ? withdrawResponse.result().transactionId().toString() : null;
    }
}
