package com.fluxbank.transaction_service.client;

import com.fluxbank.transaction_service.controller.dto.*;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "walletClient", url = "${feign-client.wallet-uri}")
public interface IWalletClient {

    @PostMapping("/deposit")
    public ResponseEntity<DepositInWalletResponse> deposit(
            @RequestBody DepositInWalletRequest request
    );

    @PostMapping("/withdraw")
    public ResponseEntity<WithDrawResponse> withdraw(
            @Valid @RequestBody WithDrawRequest request,
            @RequestHeader("X-User-Id") String userId
    );

    @PutMapping("/refund")
    public ResponseEntity<Void> refund(
            @RequestBody RefundWalletTransactionRequest request
    );

}
