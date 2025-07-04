package com.fluxbank.wallet_service.application.controller.impl;

import com.fluxbank.wallet_service.application.controller.IWalletController;
import com.fluxbank.wallet_service.application.dto.*;
import com.fluxbank.wallet_service.application.port.WalletPort;
import com.fluxbank.wallet_service.domain.models.Wallet;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
            @Valid @RequestBody CreateWalletRequest request
            // @RequestHeader("X-User-Id") String userId
    ) {
        port.createWallet(request, UUID.fromString("89d00d26-5eb1-464c-a931-965c0e09077"));

        return ResponseEntity.status(201).body(new CreateWalletResponse("Wallet criada com sucesso"));
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositInWalletResponse> deposit(
            @Valid @RequestBody DepositInWalletRequest request
    ) {
        TransactionResult result = port.deposit(request);

        return ResponseEntity.ok().body(new DepositInWalletResponse(
                result
        ));
    }

    @GetMapping("/balance")
    public ResponseEntity<GetWalletBalanceResponse> balance(
            @Valid @RequestBody GetWalletBalanceRequest request
            // @RequestHeader("X-User-Id") String userId
    ) {
        GetWalletBalanceResponse responseBody = port.balance(request, UUID.fromString("89d00d26-5eb1-464c-a931-965c0e09077"));

        return ResponseEntity.ok().body(responseBody);
    }
}
