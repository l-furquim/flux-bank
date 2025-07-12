package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.application.usecase.GetProfileUsecase;
import com.fluxbank.user_service.domain.exceptions.UserNotFoundException;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.interfaces.dto.GetUserProfileResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetProfileService implements GetProfileUsecase {

    private final UserRepository repository;

    public GetProfileService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public GetUserProfileResponse get(String userId) {
        Optional<User> userFounded = repository.getUserData(UUID.fromString(userId));

        if(userFounded.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = userFounded.get();

        return new GetUserProfileResponse(
                user.getCpf(),
                user.getFullName(),
                user.getEmail(),
                user.getBirthDate(),
                user.getAddress(),
                user.getCreatedAt()

        );
    }
}
