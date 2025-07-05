package com.fluxbank.user_service.infrastructure.persistence.mapper;

import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity user){
        return User.builder()
                .id(user.getId())
                .cpf(user.getCpf())
                .email(user.getEmail())
                .address(user.getAddress())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .fullName(user.getFullName())
                .birthDate(user.getBirthDate())
                .build();
    }

    public UserEntity toEntity(User user){
        return UserEntity.builder()
                .cpf(user.getCpf())
                .email(user.getEmail())
                .address(user.getAddress())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .birthDate(user.getBirthDate())
                .build();
    }

}
