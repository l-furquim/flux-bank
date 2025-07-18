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
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
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

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Saque realizado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Carteira para o saque não pertence ao usuario logado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Carteira esta em um estado invalido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Carteira não possui o saldo necessario",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Limite para a operação esta bloqueado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Limite insuficiente para a operação",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Carteira não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Limite não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<WithDrawResponse> withdraw(
            @Valid @RequestBody WithDrawRequest request,
            @RequestHeader("X-User_Id") String userId
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Limites bucados com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Operação invalida para a carteira",
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
    public ResponseEntity<GetWalletLimitsResponse> getLimits(
            @Valid @RequestBody GetWalletLimitsRequest request,
            @RequestHeader("X-User-Id") String userId
    );


    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reembolso realizado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados invalidos para o reembolso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Wallets não encontradas para o reembolso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "WalletTransaction não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> refund(
            @Valid @RequestBody RefundWalletTransactionRequest request
    );

}
