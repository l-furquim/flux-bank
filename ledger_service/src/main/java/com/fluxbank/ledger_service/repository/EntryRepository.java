package com.fluxbank.ledger_service.repository;

import com.fluxbank.ledger_service.domain.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EntryRepository extends JpaRepository<Entry, UUID> {
}
