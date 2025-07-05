package com.fluxbank.user_service.infrastructure.persistence.mapper;

import com.fluxbank.user_service.domain.model.PixKey;
import com.fluxbank.user_service.infrastructure.persistence.entity.PixKeyEntity;
import org.springframework.stereotype.Component;

@Component
public class PixKeyMapper {

    public PixKey toDomain(PixKeyEntity pixKey){
        return PixKey.builder()
                .id(pixKey.getId())
                .value(pixKey.getValue())
                .type(pixKey.getType())
                .issuedAt(pixKey.getIssuedAt())
                .ownerId(pixKey.getOwnerId())
                .build();
    }

    public PixKeyEntity toEntity(PixKey pixKey){
        return PixKeyEntity.builder()
                .value(pixKey.getValue())
                .type(pixKey.getType())
                .ownerId(pixKey.getOwnerId())
                .build();
    }
}
