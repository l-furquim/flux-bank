package com.fluxbank.user_service.interfaces.controller.impl;

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

    public UserControllerImpl(RegisterUserUsecase registerUsecase) {
        this.registerUsecase = registerUsecase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest servlet
    ){
        registerUsecase.register(request, new UserDeviceDto(servlet.getHeader("User-Agent")));

        return ResponseEntity.status(204).build();
    }

}
