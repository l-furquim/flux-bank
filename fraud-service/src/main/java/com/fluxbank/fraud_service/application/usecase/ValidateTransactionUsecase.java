package com.fluxbank.fraud_service.application.usecase;

import com.fluxbank.fraud_service.interfaces.dto.TransactionEventDto;

public interface ValidateTransactionUsecase {

    void validate(TransactionEventDto event);

}
