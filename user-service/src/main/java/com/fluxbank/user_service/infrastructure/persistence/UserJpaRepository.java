package com.fluxbank.user_service.infrastructure.persistence;

import com.fluxbank.user_service.infrastructure.persistence.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u from UserEntity u WHERE u.cpf = :cpf")
    Optional<UserEntity> findByCpf(@Param("cpf") String cpf);

}
