package com.fluxbank.user_service.infrastructure.persistence.mapper;

import com.fluxbank.user_service.domain.model.UserDevice;
import com.fluxbank.user_service.infrastructure.persistence.entity.UserDeviceEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDeviceMapper {

    public UserDevice toDomain(UserDeviceEntity userDevice) {
        return UserDevice.builder()
                .id(userDevice.getId())
                .userAgent(userDevice.getUserAgent())
                .userId(userDevice.getUserId())
                .build();
    }

    public UserDeviceEntity toEntity(UserDevice userDevice) {
        return UserDeviceEntity.builder()
                .userAgent(userDevice.getUserAgent())
                .userId(userDevice.getUserId())
                .build();
    }
}
