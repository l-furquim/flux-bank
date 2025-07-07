package com.fluxbank.wallet_service.application.controller;

import com.fluxbank.wallet_service.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
            @RequestHeader("X-User-Id") String userId
    );

    @Operation(
            summary = "Realiza um deposito em uma carteira",
            description = "Realiza o deposito em uma carteira"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposito realizado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Deposito invalido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Carteira de destinação não encontrada",
                    content = @Content
            )
    })
    public ResponseEntity<DepositInWalletResponse> deposit(
            @RequestBody DepositInWalletRequest request
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Balança retornada com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Busca por um deposito que não pertence ao usuário solicitado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Carteira não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<GetWalletBalanceResponse> balance(
            @RequestBody GetWalletBalanceRequest request,
            @RequestHeader("X-User-Id") String userId
    );

    public ResponseEntity<WithDrawResponse> withdraw(
            @Valid @RequestBody WithDrawRequest request,
            @RequestHeader("X-User_Id") String userId
    );

}
