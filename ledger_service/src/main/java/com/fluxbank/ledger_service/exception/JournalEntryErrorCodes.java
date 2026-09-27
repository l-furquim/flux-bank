package com.fluxbank.ledger_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JournalEntryErrorCodes {

    UNBALANCED_JOURNAL_ENTRY("UNBALANCED_JOURNAL_ENTRY", "Entry account not found with referenceId="),
    INVALID_ENTRY_ACCOUNT("INVALID_ENTRY_ACCOUNT", "Invalid entry account"),;

    private final String message;

    private final String code;


}
