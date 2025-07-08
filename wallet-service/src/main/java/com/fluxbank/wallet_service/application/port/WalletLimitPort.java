package com.fluxbank.wallet_service.application.port;


import com.fluxbank.wallet_service.application.dto.UpdateWalletLimitRequest;
import com.fluxbank.wallet_service.domain.models.Wallet;

import java.util.UUID;

public interface WalletLimitPort {

    void createInitialLimit(Wallet wallet);
    void updateWalletLimit(UpdateWalletLimitRequest request);
    void resetWalletLimit(UUID walletLimitId);

}
