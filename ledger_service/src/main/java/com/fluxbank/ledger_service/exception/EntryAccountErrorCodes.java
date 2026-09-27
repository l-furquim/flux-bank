package com.fluxbank.ledger_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EntryAccountErrorCodes {

    ENTRY_ACCOUNT_NOT_FOUND("ENTRY_ACCOUNT_NOT_FOUND", "Entry account not found with referenceId="),
    INVALID_ENTRY_ACCOUNT("INVALID_ENTRY_ACCOUNT", "Invalid entry account"),;


    private final String message;

    private final String code;



}
