package com.fluxbank.wallet_service.application.port;

import com.fluxbank.wallet_service.application.dto.CreateWalletRequest;
import com.fluxbank.wallet_service.domain.models.Wallet;

public interface WalletPort {

    Wallet createWallet(CreateWalletRequest dto, String token);

}
