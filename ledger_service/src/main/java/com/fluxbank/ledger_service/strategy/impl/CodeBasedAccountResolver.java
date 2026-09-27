package com.fluxbank.ledger_service.strategy.impl;

import com.fluxbank.ledger_service.domain.EntryAccount;
import com.fluxbank.ledger_service.dto.EntryAccountRefDTO;
import com.fluxbank.ledger_service.exception.EntryAccountErrorCodes;
import com.fluxbank.ledger_service.exception.EntryAccountNotFoundException;
import com.fluxbank.ledger_service.repository.EntryAccountRepository;
import com.fluxbank.ledger_service.strategy.EntryAccountResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CodeBasedAccountResolver implements EntryAccountResolver {

    private final EntryAccountRepository entryAccountRepository;

    @Override
    public boolean supports(EntryAccountRefDTO ref) {
        return ref.ownerType() == null && !ref.code().isBlank();
    }

    @Override
    public EntryAccount resolve(EntryAccountRefDTO ref) {
        return entryAccountRepository.findByCode(ref.code())
                .orElseThrow(() -> new EntryAccountNotFoundException(EntryAccountErrorCodes.ENTRY_ACCOUNT_NOT_FOUND));
    }
}
