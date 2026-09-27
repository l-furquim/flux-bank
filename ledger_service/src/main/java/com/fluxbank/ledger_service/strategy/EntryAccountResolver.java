package com.fluxbank.ledger_service.strategy;

import com.fluxbank.ledger_service.domain.EntryAccount;
import com.fluxbank.ledger_service.dto.EntryAccountRefDTO;

public interface EntryAccountResolver {
    boolean supports(EntryAccountRefDTO ref);
    EntryAccount resolve(EntryAccountRefDTO ref);
}
