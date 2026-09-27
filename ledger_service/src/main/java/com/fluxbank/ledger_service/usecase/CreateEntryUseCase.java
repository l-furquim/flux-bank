package com.fluxbank.ledger_service.usecase;

import com.fluxbank.ledger_service.domain.*;
import com.fluxbank.ledger_service.dto.CreateJournalEntryDTO;
import com.fluxbank.ledger_service.dto.EntryAccountRefDTO;
import com.fluxbank.ledger_service.dto.EntryDTO;
import com.fluxbank.ledger_service.mapper.EntryMapper;
import com.fluxbank.ledger_service.repository.EntryAccountRepository;
import com.fluxbank.ledger_service.repository.EntryRepository;
import com.fluxbank.ledger_service.repository.JournalEntryRepository;
import com.fluxbank.ledger_service.service.JournalBalanceValidator;
import com.fluxbank.ledger_service.strategy.EntryAccountResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreateEntryUseCase implements ICreateEntryUseCase {

    private final JournalEntryRepository journalEntryRepository;
    private final EntryRepository entryRepository;
    private final EntryAccountRepository entryAccountRepository;
    private final JournalBalanceValidator balanceValidator;
    private final List<EntryAccountResolver> resolvers;

    @Override
    @Transactional
    public void execute(CreateJournalEntryDTO request) {
        log.info("Create entry requested, txId={}, eventId={}, type={}",
                request.sourceTxId(), request.sourceEventId(), request.type());

        if (journalEntryRepository.existsBySourceEventId(request.sourceEventId())) { // TODO: Replace this via redis maybe
            log.info("This event was already processed, skipping. eventId={}",
                    request.sourceEventId());
            return;
        }

        balanceValidator.validate(request.entries());

        JournalEntry journalEntry = journalEntryRepository.save(
                JournalEntry.builder()
                        .sourceTxId(request.sourceTxId())
                        .sourceEventId(request.sourceEventId())
                        .type(request.type())
                        .description(request.description())
                        .postedAt(LocalDateTime.now())
                        .status(EntryStatus.POSTED)
                        .metadata(request.metadata())
                        .build());

        handleCreateEntries(journalEntry, request.entries());
    }

    private void handleCreateEntries(JournalEntry journalEntry, List<EntryDTO> entries) {
        for (EntryDTO entryDto : entries) {
            EntryAccount account = resolveAccount(entryDto.entryAccountRef());
            postEntry(journalEntry, account, entryDto);
        }
    }

    private EntryAccount resolveAccount(EntryAccountRefDTO ref) {
        return resolvers.stream()
                .filter(r -> r.supports(ref))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No resolver found for this account reference " + ref))
                .resolve(ref);
    }

    private void postEntry(JournalEntry journalEntry, EntryAccount account, EntryDTO dto) {
        entryRepository.save(EntryMapper.toDomain(dto, journalEntry.getId(), null, null));

        entryAccountRepository.save(account);
    }
}
