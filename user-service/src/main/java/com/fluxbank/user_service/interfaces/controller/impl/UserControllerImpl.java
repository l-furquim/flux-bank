package com.fluxbank.user_service.interfaces.controller.impl;

import com.fluxbank.user_service.application.usecase.AuthUserUsecase;
import com.fluxbank.user_service.interfaces.dto.AuthUserRequest;
import com.fluxbank.user_service.interfaces.dto.CreateUserRequest;
import com.fluxbank.user_service.interfaces.dto.UserDeviceDto;
import com.fluxbank.user_service.application.usecase.RegisterUserUsecase;
import com.fluxbank.user_service.interfaces.controller.UserController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserControllerImpl implements UserController {

    private final RegisterUserUsecase registerUsecase;
    private final AuthUserUsecase authUsecase;

    public UserControllerImpl(RegisterUserUsecase registerUsecase, AuthUserUsecase authUsecase) {
        this.registerUsecase = registerUsecase;
        this.authUsecase = authUsecase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest servlet
    ){
        registerUsecase.register(request, new UserDeviceDto(servlet.getHeader("User-Agent")));

        return ResponseEntity.status(204).build();
    }

    @PostMapping("/auth")
    public ResponseEntity<String> authUser(
            @Valid @RequestBody AuthUserRequest request
    ) {
        authUsecase.auth(request);

        return ResponseEntity.ok().body("Login realizado com sucesso");
    }


}
