package com.fluxbank.ledger_service.dto;

import com.fluxbank.commons_core.domain.Metadata;
import com.fluxbank.ledger_service.domain.EntryType;

import java.util.List;
import java.util.UUID;

public record CreateJournalEntryDTO(

        UUID sourceEventId,
        UUID sourceTxId,
        EntryType type,
        String description,
        List<EntryDTO> entries,
        Metadata metadata

) {
}
