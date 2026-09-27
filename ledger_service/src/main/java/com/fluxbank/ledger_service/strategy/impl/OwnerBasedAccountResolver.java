package com.fluxbank.ledger_service.strategy.impl;

import com.fluxbank.ledger_service.domain.EntryAccount;
import com.fluxbank.ledger_service.dto.EntryAccountRefDTO;
import com.fluxbank.ledger_service.exception.EntryAccountErrorCodes;
import com.fluxbank.ledger_service.exception.InvalidEntryAccountException;
import com.fluxbank.ledger_service.repository.EntryAccountRepository;
import com.fluxbank.ledger_service.strategy.EntryAccountResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OwnerBasedAccountResolver implements EntryAccountResolver {

    private final EntryAccountRepository entryAccountRepository;

    @Override
    public boolean supports(EntryAccountRefDTO ref) {
        return ref.ownerType() != null;
    }

    @Override
    public EntryAccount resolve(EntryAccountRefDTO ref) {
        if (Objects.isNull(ref)) {
            throw new InvalidEntryAccountException(ref.ownerReferenceId().toString(), EntryAccountErrorCodes.INVALID_ENTRY_ACCOUNT);
        }

        return entryAccountRepository
                .findByOwnerTypeAndOwnerReferenceIdAndCategory(
                        ref.ownerType(), ref.ownerReferenceId(), ref.category())
                .orElseGet(() -> createOwnerAccount(ref));
    }

    private EntryAccount createOwnerAccount(EntryAccountRefDTO ref) {
        EntryAccount account = EntryAccount.builder()
                .ownerType(ref.ownerType())
                .ownerReferenceId(ref.ownerReferenceId())
                .category(ref.category())
                .accountClass(ref.category().getAccountClass())
                .currency(ref.currency())
                .build();

        try {
            return entryAccountRepository.saveAndFlush(account);
        } catch (DataIntegrityViolationException e) {
            return entryAccountRepository
                    .findByOwnerTypeAndOwnerReferenceIdAndCategory(
                            ref.ownerType(), ref.ownerReferenceId(), ref.category())
                    .orElseThrow();
        }
    }
}
