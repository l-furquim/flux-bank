package com.fluxbank.wallet_service.application.port;

import com.fluxbank.wallet_service.application.dto.CreateWalletTransactionDto;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;

public interface WalletTransactionPort {
    WalletTransaction create(CreateWalletTransactionDto data);
}
