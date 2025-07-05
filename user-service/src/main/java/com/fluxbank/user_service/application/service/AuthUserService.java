package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.application.usecase.AuthUserUsecase;
import com.fluxbank.user_service.domain.exceptions.UnauthorizedAuthException;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.repository.UserDeviceRepository;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.domain.service.CacheService;
import com.fluxbank.user_service.domain.service.TokenService;
import com.fluxbank.user_service.interfaces.dto.AuthUserRequest;
import com.fluxbank.user_service.interfaces.dto.UserTokenData;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthUserService implements AuthUserUsecase {

    private final UserDeviceRepository userDeviceRepository;
    private final UserRepository repository;
    private final TokenService tokenService;
    private final CacheService cacheService;
    private final PasswordEncoder encoder;

    public AuthUserService(UserDeviceRepository userDeviceRepository, UserRepository repository, TokenService tokenService, CacheService cacheService, PasswordEncoder encoder) {
        this.userDeviceRepository = userDeviceRepository;
        this.repository = repository;
        this.tokenService = tokenService;
        this.cacheService = cacheService;
        this.encoder = encoder;
    }

    @Override
    public void auth(AuthUserRequest request) {
        Optional<User> userFounded = repository.findByCpf(request.cpf());

        if(userFounded.isEmpty()) {
            throw new UnauthorizedAuthException();
        }

        User user = userFounded.get();

        boolean passwordMatch = encoder.matches(request.password(), user.getPassword());

        if(!passwordMatch) {
            throw new UnauthorizedAuthException();
        }

        String token = tokenService.generateToken(user);

        // implementar registro do device id certo...

        UserTokenData tokenData = UserTokenData.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .deviceId(" aaaaa")
                        .expiresAt(tokenService.getExpirationDate(token))
                        .issuedAt(Instant.now())
                        .build();


        cacheService.cacheToken(token, tokenData);
    }
}
