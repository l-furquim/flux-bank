package com.fluxbank.ledger_service.exception;

import com.fluxbank.commons_web.exception.UnprocessableEntityException;

public class UnbalancedJournalEntryException extends UnprocessableEntityException {
    public UnbalancedJournalEntryException(JournalEntryErrorCodes errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }
}
