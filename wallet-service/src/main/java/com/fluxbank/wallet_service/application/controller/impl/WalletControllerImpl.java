package com.fluxbank.wallet_service.application.controller.impl;

import com.fluxbank.wallet_service.application.controller.IWalletController;
import com.fluxbank.wallet_service.application.dto.CreateWalletRequest;
import com.fluxbank.wallet_service.application.dto.CreateWalletResponse;
import com.fluxbank.wallet_service.application.port.WalletPort;
import com.fluxbank.wallet_service.domain.models.Wallet;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@SecurityRequirement(name = "bearerAuth")
public class WalletControllerImpl implements IWalletController {

    private final WalletPort port;

    public WalletControllerImpl(WalletPort port) {
        this.port = port;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateWalletResponse> create(
            @Valid @RequestBody CreateWalletRequest request,
            @RequestHeader(value="Authorization") String malformedToken
    ) {
        Wallet wallet = port.createWallet(request, malformedToken.split("Bearer ")[1]);

        return ResponseEntity.status(201).body(new CreateWalletResponse(wallet));
    }


}
