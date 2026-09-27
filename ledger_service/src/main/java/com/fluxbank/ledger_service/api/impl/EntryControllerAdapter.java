package com.fluxbank.ledger_service.api.impl;

import com.fluxbank.ledger_service.api.EntryApi;
import com.fluxbank.ledger_service.domain.Entry;
import com.fluxbank.ledger_service.dto.CreateJournalEntryDTO;
import com.fluxbank.ledger_service.usecase.ICreateEntryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/entries")
public class EntryControllerAdapter implements EntryApi {

    private final ICreateEntryUseCase createEntryUseCase;

    @Override
    public ResponseEntity<Void> createEntry(CreateJournalEntryDTO request) {
        this.createEntryUseCase.execute(request);

        return ResponseEntity.status(201).build();
    }

}
