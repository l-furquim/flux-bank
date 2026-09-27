package com.fluxbank.ledger_service.dto;

import com.fluxbank.commons_core.domain.Metadata;
import com.fluxbank.ledger_service.domain.EntryDirection;
import com.fluxbank.ledger_service.domain.EntryType;

import java.math.BigDecimal;


public record EntryDTO(

        EntryAccountRefDTO entryAccountRef,
        EntryDirection direction,
        BigDecimal amount,
        EntryType type,
        Metadata metadata

) {
}
