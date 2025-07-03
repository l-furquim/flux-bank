package com.fluxbank.wallet_service.application.port;

import com.fluxbank.wallet_service.application.dto.CreateWalletRequest;
import com.fluxbank.wallet_service.application.dto.DepositInWalletRequest;
import com.fluxbank.wallet_service.application.dto.TransactionResult;
import com.fluxbank.wallet_service.domain.models.Wallet;

import java.util.UUID;

public interface WalletPort {

    void createWallet(CreateWalletRequest dto, UUID userId);
    TransactionResult deposit(DepositInWalletRequest data);
}
