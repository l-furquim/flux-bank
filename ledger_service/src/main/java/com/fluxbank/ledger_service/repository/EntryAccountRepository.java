package com.fluxbank.ledger_service.repository;

import com.fluxbank.ledger_service.domain.AccountOwnerType;
import com.fluxbank.ledger_service.domain.EntryAccount;
import com.fluxbank.ledger_service.domain.LedgerCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntryAccountRepository extends JpaRepository<EntryAccount, UUID> {

    Optional<EntryAccount> findByCode(String code);
    Optional<EntryAccount> findByOwnerTypeAndOwnerReferenceIdAndCategory(AccountOwnerType ownerType, UUID ownerReferenceId, LedgerCategory category);

}
