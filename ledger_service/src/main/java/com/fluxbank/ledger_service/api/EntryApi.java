package com.fluxbank.ledger_service.api;

import com.fluxbank.commons_web.annotations.Route;
import com.fluxbank.ledger_service.dto.CreateJournalEntryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


public interface EntryApi {

    @Route
    @PostMapping
    public ResponseEntity<Void> createEntry(
            @RequestBody CreateJournalEntryDTO request
    );


}
