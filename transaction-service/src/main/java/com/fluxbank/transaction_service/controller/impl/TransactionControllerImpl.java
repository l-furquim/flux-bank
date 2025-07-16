package com.fluxbank.transaction_service.controller.impl;

import com.fluxbank.transaction_service.controller.dto.SendPixRequest;
import com.fluxbank.transaction_service.controller.dto.SendPixResponse;
import com.fluxbank.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionControllerImpl {

    private final TransactionService transactionService;

    public TransactionControllerImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/pix/send")
    public ResponseEntity<SendPixResponse> sendPix(
            @Valid @RequestBody SendPixRequest request,
            @RequestHeader("X-User-Id") String userId
    ){
        SendPixResponse response = transactionService.sendPix(request, userId);

        return ResponseEntity.accepted().body(response);
    }


}
