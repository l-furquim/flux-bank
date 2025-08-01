package com.fluxbank.user_service.infrastructure.persistence;

import com.fluxbank.user_service.infrastructure.persistence.entity.PixKeyEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PixKeyJpaRepository extends JpaRepository<PixKeyEntity, UUID> {

    @Query("SELECT p FROM PixKeyEntity p WHERE p.ownerId = :userId")
    List<PixKeyEntity> findByUserId(@Param("userId") UUID userId);

    @Query("DELETE FROM PixKeyEntity p WHERE p.id = :id")
    void deletePixKeyById(@Param("id") UUID id);

    @Query("SELECT p FROM PixKeyEntity p WHERE p.ownerId = :userId AND p.type = 'CPF'")
    Optional<PixKeyEntity> findCpfKeyByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM PixKeyEntity p WHERE p.value = :value")
    Optional<PixKeyEntity> findByValue(@Param("value") String value);

}
