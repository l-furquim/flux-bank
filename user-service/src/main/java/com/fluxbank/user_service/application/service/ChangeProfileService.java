package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.application.usecase.ChangeProfileUsecase;
import com.fluxbank.user_service.domain.exceptions.UserNotFoundException;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.interfaces.dto.ChangeUserProfileRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ChangeProfileService implements ChangeProfileUsecase {

    private final UserRepository repository;

    public ChangeProfileService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void change(ChangeUserProfileRequest request, String userId) {
        Optional<User> userFounded = repository.getUserData(UUID.fromString(userId));

        if(userFounded.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = userFounded.get();

        if(request.address() != null) {
            user.setAddress(request.address());
        }

        if(request.email() != null){
            user.setEmail(request.email());
        }

        if(request.fullName() != null) {
            user.setFullName(request.fullName());
        }

        repository.updateUserData(user);
    }
}
