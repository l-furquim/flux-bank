package com.fluxbank.user_service.interfaces.controller;

import com.fluxbank.user_service.interfaces.dto.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Chave pix criada com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario autenticado não encontrado no banco.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou faltantes para a criação da chave.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Chave ja existente com esse valor ou chave do tipo cpf ja criada no banco.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> createPixKey(
            @Valid @RequestBody CreatePixKeyRequest request,
            @RequestHeader("X-User-Id") String userId
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca realizada com sucesso",
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
    public ResponseEntity<GetUserPixKeysResponse> getPixKeys(
            @RequestHeader("X-User-Id") String userId
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Chave resolvida com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Chave não encontrada.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<ResolvePixKeyResponse> resolvePixKey(
            @PathVariable("keyValue") String keyValue
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados do usuário buscados com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno não mapeado",
                    content = @Content
            )
    })
    public ResponseEntity<GetUserDataResponse> getUserData(
            @PathVariable("userId") String userId
    );

}
