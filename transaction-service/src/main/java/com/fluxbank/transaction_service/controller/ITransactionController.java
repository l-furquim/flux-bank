package com.fluxbank.transaction_service.controller;

import com.fluxbank.transaction_service.controller.dto.GetTransactionHistoryResponse;
import com.fluxbank.transaction_service.controller.dto.SendPixRequest;
import com.fluxbank.transaction_service.controller.dto.SendPixResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface ITransactionController {

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Transação iniciada com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Informações inválidas para a criação da transação.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<SendPixResponse> sendPix(
            @Valid @RequestBody SendPixRequest request,
            @RequestHeader("X-User-Id") String userId
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historicos buscados com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Numero de paginas invalidos para busca",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })public ResponseEntity<GetTransactionHistoryResponse> history(
            @PathVariable("start") int start,
            @PathVariable("end") int end,
            @RequestHeader("X-User-Id") String userId
    );

}
