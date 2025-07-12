package com.fluxbank.wallet_service.application.controller.impl;

import com.fluxbank.wallet_service.application.controller.IWalletController;
import com.fluxbank.wallet_service.application.dto.*;
import com.fluxbank.wallet_service.application.port.WalletPort;
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
            @Valid @RequestBody CreateWalletRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        port.createWallet(request, UUID.fromString(userId));

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
            @Valid @RequestBody GetWalletBalanceRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        GetWalletBalanceResponse responseBody = port.balance(request, UUID.fromString(userId));

        return ResponseEntity.ok().body(responseBody);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithDrawResponse> withdraw(
            @Valid @RequestBody WithDrawRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        TransactionResult result = port.withDraw(request, userId);

        return ResponseEntity.ok().body(new WithDrawResponse(result));
    }

    @GetMapping("/limits")
    public ResponseEntity<GetWalletLimitsResponse> getLimits(
            @Valid @RequestBody GetWalletLimitsRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        GetWalletLimitsResponse response = port.getLimits(request, UUID.fromString(userId));

        return ResponseEntity.ok().body(response);
    }


}
