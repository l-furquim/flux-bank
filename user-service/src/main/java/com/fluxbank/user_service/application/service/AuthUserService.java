package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.application.usecase.AuthUserUsecase;
import com.fluxbank.user_service.domain.exceptions.UnauthorizedAuthException;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.model.UserDevice;
import com.fluxbank.user_service.domain.repository.UserDeviceRepository;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.domain.service.CacheService;
import com.fluxbank.user_service.domain.service.TokenService;
import com.fluxbank.user_service.interfaces.dto.AuthUserRequest;
import com.fluxbank.user_service.interfaces.dto.AuthUserResponse;
import com.fluxbank.user_service.interfaces.dto.UserTokenData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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
    public AuthUserResponse auth(AuthUserRequest request, String userAgent) {
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


        List<UserDevice> userDevices = userDeviceRepository.findByUserId(user.getId());
        log.info("User devices: {}", userDevices);

        UserDevice userDevice = null;

        List<UserDevice> userDevicesWithActualAgent = userDevices
                .stream()
                .filter(d -> d.getUserAgent().equals(userAgent))
                .toList();

        if(userDevicesWithActualAgent.size() == 1) {
            userDevice = userDevicesWithActualAgent.get(0);
        } else {
            UserDevice newDevice = UserDevice.builder()
                    .userId(user.getId())
                    .userAgent(userAgent)
                    .build();

            userDevice = userDeviceRepository.createUserDevice(newDevice);
        }

        UserTokenData tokenData = UserTokenData.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .deviceId(String.valueOf(userDevice.getId()))
                        .expiresAt(tokenService.getExpirationDate(token))
                        .issuedAt(Instant.now())
                        .build();


        cacheService.cacheToken(token, tokenData);

        return new AuthUserResponse(token, tokenData);
    }
}
