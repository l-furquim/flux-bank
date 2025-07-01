package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.AddPaymentMethodDto;
import com.fluxbank.wallet_service.domain.enums.MethodStatus;
import com.fluxbank.wallet_service.domain.models.PaymentMethods;

import java.time.LocalDateTime;


public class PaymentMethodDomainService {

    private final WalletDomainService walletDomainService;

    public PaymentMethodDomainService(WalletDomainService walletDomainService) {
        this.walletDomainService = walletDomainService;
    }

    public PaymentMethods addPaymentMethod(AddPaymentMethodDto dto) {

//        PaymentMethods paymentMethods = PaymentMethods.builder()
//               .lastFourDigits(dto.lastFourDigits())
//               .methodType(dto.methodType())
//               .createdAt(LocalDateTime.now())
//               .displayName(dto.displayName())
//               .expiryDate(dto.expireDate())
//               .isDefault(dto.setAsDefault())
//               .metadata(dto.metadata())
//               .status(MethodStatus.ACTIVE)
//                .wallet()
//               .build();


        return null;
    }

}
