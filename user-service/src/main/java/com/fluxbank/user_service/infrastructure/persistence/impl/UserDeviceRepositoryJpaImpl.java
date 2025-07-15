package com.fluxbank.user_service.infrastructure.persistence.impl;

import com.fluxbank.user_service.domain.model.UserDevice;
import com.fluxbank.user_service.domain.repository.UserDeviceRepository;
import com.fluxbank.user_service.infrastructure.persistence.UserDeviceJpaRepository;
import com.fluxbank.user_service.infrastructure.persistence.entity.UserDeviceEntity;
import com.fluxbank.user_service.infrastructure.persistence.mapper.UserDeviceMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserDeviceRepositoryJpaImpl implements UserDeviceRepository {

    private final UserDeviceJpaRepository repository;
    private final UserDeviceMapper mapper;

    public UserDeviceRepositoryJpaImpl(UserDeviceJpaRepository repository, UserDeviceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserDevice createUserDevice(UserDevice device) {
        UserDeviceEntity deviceToBePersisted = mapper.toEntity(device);

        UserDeviceEntity devicePersisted = repository.save(deviceToBePersisted);

        return mapper.toDomain(devicePersisted);
    }

    @Override
    public List<UserDevice> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
