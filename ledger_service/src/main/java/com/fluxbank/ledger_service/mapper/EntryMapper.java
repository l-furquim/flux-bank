package com.fluxbank.ledger_service.mapper;


import com.fluxbank.ledger_service.domain.Entry;
import com.fluxbank.ledger_service.dto.EntryDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EntryMapper {

    public static Entry toDomain(
            EntryDTO entryDTO,
            UUID journalEntryId,
            UUID sourceEntryId,
            UUID previousEntryId
    ) {
        return Entry.builder()
                .accountOwnerType(entryDTO.entryAccountRef().ownerType())
                .amount(entryDTO.amount())
                .metadata(entryDTO.metadata())
                .balanceAfter(null)
                .balanceBefore(null)
                .entryAccountId(entryDTO.entryAccountRef().ownerReferenceId())
                .entryDirection(entryDTO.direction())
                .previousEntryId(previousEntryId)
                .sourceEntryId(sourceEntryId)
                .journalEntryId(journalEntryId)
                .reversesEntryId(null)
                .build();
    }

}
