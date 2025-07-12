package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.application.usecase.GetUserPixKeysUsecase;
import com.fluxbank.user_service.domain.exceptions.UserNotFoundException;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.repository.PixKeyRepository;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.interfaces.dto.GetUserPixKeysResponse;
import com.fluxbank.user_service.interfaces.dto.PixKeyInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetUserPixKeysService implements GetUserPixKeysUsecase {

    private final PixKeyRepository pixKeyRepository;
    private final UserRepository userRepository;

    public GetUserPixKeysService(PixKeyRepository pixKeyRepository, UserRepository userRepository) {
        this.pixKeyRepository = pixKeyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GetUserPixKeysResponse get(String userId) {
        UUID userIdFormatted = UUID.fromString(userId);

        Optional<User> userFounded = userRepository.getUserData(userIdFormatted);

        if (userFounded.isEmpty()) {
            throw new UserNotFoundException();
        }

        List<PixKeyInfoDto> keys = pixKeyRepository
                .findByUserId(userIdFormatted)
                .stream()
                .map(k -> {
                    return new PixKeyInfoDto(
                            k.getType(),
                            k.getValue()
                    );
                })
                .toList();

        return new GetUserPixKeysResponse(keys);
    }
}
