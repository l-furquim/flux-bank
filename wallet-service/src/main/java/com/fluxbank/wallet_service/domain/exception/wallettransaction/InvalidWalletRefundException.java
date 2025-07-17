package com.fluxbank.wallet_service.domain.exception.wallettransaction;

public class InvalidWalletRefundException extends RuntimeException {
    public InvalidWalletRefundException(String message) {
        super(message);
    }
}
