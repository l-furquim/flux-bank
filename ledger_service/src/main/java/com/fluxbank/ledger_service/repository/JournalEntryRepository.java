package com.fluxbank.ledger_service.repository;

import com.fluxbank.ledger_service.domain.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {

    boolean existsBySourceEventId(UUID sourceEventId);

}
