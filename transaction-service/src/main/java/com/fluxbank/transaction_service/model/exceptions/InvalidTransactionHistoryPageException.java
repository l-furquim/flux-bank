package com.fluxbank.transaction_service.model.exceptions;

public class InvalidTransactionHistoryPageException extends RuntimeException {
    public InvalidTransactionHistoryPageException() {
        super("Invalid page request for user transaction history");
    }
}
