package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.application.usecase.GetUserDataUsecase;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.domain.exceptions.UserNotFoundException;
import com.fluxbank.user_service.interfaces.dto.GetUserDataResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetUserDataService implements GetUserDataUsecase {

    private final UserRepository repository;

    public GetUserDataService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable(value = "userData", key = "#userId")
    public GetUserDataResponse get(String userId) {
        Optional<User> user = repository.findById(UUID.fromString(userId));

        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        User userFound = user.get();
        return new GetUserDataResponse(
                userFound.getId(),
                userFound.getEmail(),
                userFound.getFullName(),
                userFound.getCpf()
        );
    }
}