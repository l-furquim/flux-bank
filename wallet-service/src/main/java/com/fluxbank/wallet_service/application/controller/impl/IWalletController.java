package com.fluxbank.wallet_service.application.controller.impl;

import com.fluxbank.wallet_service.domain.models.Wallet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IWalletController {

    @Operation(
            summary = "Criar nova carteira",
            description = "Cria uma nova carteira para o usuário especificado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Carteira criada com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Carteira já existe",
                    content = @Content
            )
    })
    public ResponseEntity<Wallet> createWallet(@RequestBody )

}
