package com.fluxbank.user_service.domain.repository;

import com.fluxbank.user_service.domain.model.UserDevice;

import java.util.List;
import java.util.UUID;

public interface UserDeviceRepository {

    void createUserDevice(UserDevice device);
    List<UserDevice> findByUserId(UUID userId);

}
