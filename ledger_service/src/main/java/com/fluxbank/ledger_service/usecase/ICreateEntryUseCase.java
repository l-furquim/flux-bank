package com.fluxbank.ledger_service.usecase;

import com.fluxbank.ledger_service.dto.CreateJournalEntryDTO;

public interface ICreateEntryUseCase {

    void execute(CreateJournalEntryDTO request);

}
