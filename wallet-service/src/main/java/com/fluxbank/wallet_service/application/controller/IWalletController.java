package com.fluxbank.wallet_service.application.controller;

import com.fluxbank.wallet_service.application.dto.CreateWalletRequest;
import com.fluxbank.wallet_service.application.dto.CreateWalletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

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
                    description = "Carteira com essa moeda já existe",
                    content = @Content
            )
    })
    public ResponseEntity<CreateWalletResponse> create(
            @RequestBody CreateWalletRequest request,
            @RequestHeader(value="Authorization") String malformedToken
    );

}
