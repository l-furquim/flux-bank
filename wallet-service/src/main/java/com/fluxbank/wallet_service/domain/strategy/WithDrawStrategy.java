package com.fluxbank.wallet_service.domain.strategy;

import com.fluxbank.wallet_service.application.dto.WithDrawResponse;
import com.fluxbank.wallet_service.domain.enums.TransactionType;

public interface WithDrawStrategy {

    boolean supports(TransactionType type);
    WithDrawResponse process();

}
