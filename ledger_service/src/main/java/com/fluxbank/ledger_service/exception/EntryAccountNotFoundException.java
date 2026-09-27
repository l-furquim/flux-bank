package com.fluxbank.ledger_service.exception;

import com.fluxbank.commons_web.exception.NotFoundException;

public class EntryAccountNotFoundException extends NotFoundException {
    public EntryAccountNotFoundException(EntryAccountErrorCodes errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }
}
