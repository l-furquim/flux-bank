package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.CreateWalletTransactionDto;
import com.fluxbank.wallet_service.application.port.WalletTransactionPort;
import org.springframework.stereotype.Service;

@Service
public class WalletTransactionDomainService implements WalletTransactionPort {



    @Override
    public void create(CreateWalletTransactionDto data) {

    }
}
