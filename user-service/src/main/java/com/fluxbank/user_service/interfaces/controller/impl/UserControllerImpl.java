package com.fluxbank.user_service.interfaces.controller.impl;

import com.fluxbank.user_service.application.usecase.*;
import com.fluxbank.user_service.interfaces.dto.*;
import com.fluxbank.user_service.interfaces.controller.UserController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/users")
public class UserControllerImpl implements UserController {

    private final RegisterUserUsecase registerUsecase;
    private final AuthUserUsecase authUsecase;
    private final GetProfileUsecase getProfileUsecase;
    private final ChangeProfileUsecase changeProfileUsecase;
    private final CreatePixKeyUsecase createPixUsecase;
    private final GetUserPixKeysUsecase getPixKeysUsecase;
    private final ResolvePixKeyUsecase resolvePixKeyUsecase;
    private final GetUserDataUsecase getUserDataUsecase;

    public UserControllerImpl(RegisterUserUsecase registerUsecase, AuthUserUsecase authUsecase, GetProfileUsecase getProfileUsecase, ChangeProfileUsecase changeProfileUsecase, CreatePixKeyUsecase createPixUsecase, GetUserPixKeysUsecase getPixKeysUsecase, ResolvePixKeyUsecase resolvePixKeyUsecase, GetUserDataUsecase getUserDataUsecase) {
        this.registerUsecase = registerUsecase;
        this.authUsecase = authUsecase;
        this.getProfileUsecase = getProfileUsecase;
        this.changeProfileUsecase = changeProfileUsecase;
        this.createPixUsecase = createPixUsecase;
        this.getPixKeysUsecase = getPixKeysUsecase;
        this.resolvePixKeyUsecase = resolvePixKeyUsecase;
        this.getUserDataUsecase = getUserDataUsecase;
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
            @Valid @RequestBody AuthUserRequest request,
            @RequestHeader("User-Agent") String agent
    ) {
        AuthUserResponse response = authUsecase.auth(request, agent);

        ResponseCookie sessionCookie = ResponseCookie.from("SESSION", response.token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.between(Instant.now(), response.tokenData().getExpiresAt()))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body("Login realizado com sucesso");
    }

    @GetMapping("/profile")
    public ResponseEntity<GetUserProfileResponse> getProfile(
            @RequestHeader("X-User-Id") String userId
    ) {
        GetUserProfileResponse response = getProfileUsecase.get(userId);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> changeProfile(
            @Valid @RequestBody ChangeUserProfileRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        changeProfileUsecase.change(request, userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pix-keys")
    public ResponseEntity<Void> createPixKey(
            @Valid @RequestBody CreatePixKeyRequest request,
            @RequestHeader("X-User-Id") String userId
    ){
        createPixUsecase.create(request, userId);

        return ResponseEntity.status(201).build();
    }

    @GetMapping("/pix-keys")
    public ResponseEntity<GetUserPixKeysResponse> getPixKeys(
            @RequestHeader("X-User-Id") String userId
    ){
        GetUserPixKeysResponse response = getPixKeysUsecase.get(userId);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/pix-keys/{keyValue}")
    public ResponseEntity<ResolvePixKeyResponse> resolvePixKey(
            @PathVariable("keyValue") String keyValue
    ) {
        ResolvePixKeyResponse response = resolvePixKeyUsecase.resolve(keyValue);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserDataResponse> getUserData(
            @PathVariable("userId") String userId
    ) {
        GetUserDataResponse response = getUserDataUsecase.get(userId);

        return ResponseEntity.ok().body(response);
    }

}
