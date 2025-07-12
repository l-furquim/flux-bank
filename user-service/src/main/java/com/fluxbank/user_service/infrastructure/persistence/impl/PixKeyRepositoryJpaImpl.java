package com.fluxbank.user_service.infrastructure.persistence.impl;

import com.fluxbank.user_service.domain.model.PixKey;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.repository.PixKeyRepository;
import com.fluxbank.user_service.infrastructure.persistence.PixKeyJpaRepository;
import com.fluxbank.user_service.infrastructure.persistence.entity.PixKeyEntity;
import com.fluxbank.user_service.infrastructure.persistence.mapper.PixKeyMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PixKeyRepositoryJpaImpl implements PixKeyRepository {

    private final PixKeyJpaRepository repository;
    private final PixKeyMapper mapper;

    public PixKeyRepositoryJpaImpl(PixKeyJpaRepository repository, PixKeyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void createPixKey(PixKey pixkey) {
        PixKeyEntity keyToBePersisted = mapper.toEntity(pixkey);

        repository.save(keyToBePersisted);
    }

    @Override
    public List<PixKey> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Transactional
    @Override
    public void updatePixKey(PixKey pixKey) {
        PixKeyEntity pixToBeUpdated = mapper.toEntity(pixKey);

        pixToBeUpdated.setId(pixKey.getId());
        pixToBeUpdated.setIssuedAt(pixKey.getIssuedAt());

        repository.save(pixToBeUpdated);
    }

    @Transactional
    @Override
    public void deletePixKey(UUID pixKeyId) {
        repository.deletePixKeyById(pixKeyId);
    }

    @Override
    public PixKey findCpfKeyByUser(User user) {
        Optional<PixKeyEntity> key = repository.findCpfKeyByUserId(user.getId());

        return key.map(mapper::toDomain).orElse(null);
    }
}
