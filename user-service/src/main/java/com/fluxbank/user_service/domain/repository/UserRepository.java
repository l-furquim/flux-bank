package com.fluxbank.user_service.domain.repository;

import com.fluxbank.user_service.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByCpf(String cpf);
    void createUser(User user);
    Optional<User> getUserData(UUID userId);

}
