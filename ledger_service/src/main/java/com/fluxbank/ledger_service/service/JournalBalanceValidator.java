package com.fluxbank.ledger_service.service;

import com.fluxbank.ledger_service.domain.EntryDirection;
import com.fluxbank.ledger_service.dto.EntryDTO;
import com.fluxbank.ledger_service.exception.JournalEntryErrorCodes;
import com.fluxbank.ledger_service.exception.UnbalancedJournalEntryException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class JournalBalanceValidator {

    public void validate(List<EntryDTO> entries) {
        BigDecimal debitTotal = sumByDirection(entries, EntryDirection.DEBIT);
        BigDecimal creditTotal = sumByDirection(entries, EntryDirection.CREDIT);

        if (!debitTotal.equals(creditTotal)) {
            throw new UnbalancedJournalEntryException(JournalEntryErrorCodes.UNBALANCED_JOURNAL_ENTRY);
        }
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("Journal entry must have at least two entries"); // TODO: Replace with a bad request exception
        }
    }

    private BigDecimal sumByDirection(List<EntryDTO> entries, EntryDirection direction) {
        return entries.stream()
                .filter(e -> e.direction() == direction)
                .map(EntryDTO::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
