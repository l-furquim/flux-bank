package com.fluxbank.wallet_service.application.port;

import com.fluxbank.wallet_service.application.dto.CreateWalletTransactionDto;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;

import java.util.UUID;

public interface WalletTransactionPort {
    WalletTransaction create(CreateWalletTransactionDto data);
    WalletTransaction findById(UUID id);
    void createRefundTransaction(WalletTransaction transactionToBeRefunded, Wallet payeeWallet);

}
