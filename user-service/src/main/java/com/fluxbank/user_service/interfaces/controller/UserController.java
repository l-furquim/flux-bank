package com.fluxbank.user_service.interfaces.controller;

import com.fluxbank.user_service.interfaces.dto.AuthUserRequest;
import com.fluxbank.user_service.interfaces.dto.ChangeUserProfileRequest;
import com.fluxbank.user_service.interfaces.dto.CreateUserRequest;
import com.fluxbank.user_service.interfaces.dto.GetUserProfileResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface UserController {

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario criado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados invalidos ou não suportados para a criação de um usuário",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    ResponseEntity<Void> registerUser(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest servlet
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Credenciais incorretas.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<String> authUser(
            @Valid @RequestBody AuthUserRequest request,
            @RequestHeader("User-Agent") String agent
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados buscados com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario autenticado não encontrado no banco.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<GetUserProfileResponse> getProfile(
            @RequestHeader("X-User-Id") String userId
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Dados atualizados com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario autenticado não encontrado no banco.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> changeProfile(
            @Valid @RequestHeader ChangeUserProfileRequest request,
            @RequestHeader("X-User-Id") String userId
    );


}
