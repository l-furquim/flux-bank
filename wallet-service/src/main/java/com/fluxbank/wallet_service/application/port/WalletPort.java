package com.fluxbank.wallet_service.application.port;

import com.fluxbank.wallet_service.application.dto.*;
import com.fluxbank.wallet_service.domain.models.Wallet;

import java.util.UUID;

public interface WalletPort {

    Wallet createWallet(CreateWalletRequest dto, UUID userId);
    TransactionResult deposit(DepositInWalletRequest data);
    GetWalletBalanceResponse balance(GetWalletBalanceRequest request, UUID userId);
    TransactionResult withDraw(WithDrawRequest request, String userId);
    GetWalletLimitsResponse getLimits(GetWalletLimitsRequest request, UUID userId);

}
