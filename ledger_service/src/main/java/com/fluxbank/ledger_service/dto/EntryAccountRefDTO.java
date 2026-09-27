package com.fluxbank.ledger_service.dto;

import com.fluxbank.commons_core.domain.Currency;
import com.fluxbank.ledger_service.domain.AccountOwnerType;
import com.fluxbank.ledger_service.domain.LedgerCategory;

import java.util.UUID;

public record EntryAccountRefDTO(
        String code,
        Currency currency,
        LedgerCategory category,
        UUID ownerReferenceId,
        AccountOwnerType  ownerType
) {
}
