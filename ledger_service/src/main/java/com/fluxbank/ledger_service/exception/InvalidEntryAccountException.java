package com.fluxbank.ledger_service.exception;

import com.fluxbank.commons_web.exception.UnprocessableEntityException;

public class InvalidEntryAccountException extends UnprocessableEntityException {
    public InvalidEntryAccountException(String referenceId, EntryAccountErrorCodes errorCode) {
        super(errorCode.getCode(), errorCode.getMessage() + referenceId);
    }
}
