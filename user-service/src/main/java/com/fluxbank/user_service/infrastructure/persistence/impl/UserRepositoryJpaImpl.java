package com.fluxbank.user_service.infrastructure.persistence.impl;

import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.infrastructure.persistence.UserJpaRepository;
import com.fluxbank.user_service.infrastructure.persistence.entity.UserEntity;
import com.fluxbank.user_service.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryJpaImpl implements UserRepository {

    private final UserJpaRepository repository;
    private final UserMapper mapper;

    public UserRepositoryJpaImpl(UserJpaRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        Optional<UserEntity> persistedUser = repository.findByCpf(cpf);

        return persistedUser.map(mapper::toDomain);
    }

    @Override
    public void createUser(User user) {
        UserEntity userToBePersisted = mapper.toEntity(user);

        repository.save(userToBePersisted);
    }

    @Override
    public Optional<User> getUserData(UUID userId) {
        Optional<UserEntity> persistedUser = repository.findById(userId);

        return persistedUser.map(mapper::toDomain);
    }
}
