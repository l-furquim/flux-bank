package com.fluxbank.wallet_service.application.port;

import com.fluxbank.wallet_service.application.dto.CreateWalletTransactionDto;

public interface WalletTransactionPort {
    void create(CreateWalletTransactionDto data);
}
