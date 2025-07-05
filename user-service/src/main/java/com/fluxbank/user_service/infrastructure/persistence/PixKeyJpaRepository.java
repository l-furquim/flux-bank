package com.fluxbank.user_service.infrastructure.persistence;

import com.fluxbank.user_service.infrastructure.persistence.entity.PixKeyEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PixKeyJpaRepository extends JpaRepository<PixKeyEntity, UUID> {

    @Query("SELECT p FROM PixKeyEntity WHERE p.userId = :userId")
    List<PixKeyEntity> findByUserId(@Param("userId") UUID userId);

    @Query("DELETE FROM PixKeyEntity p WHERE p.id = :id")
    void deletePixKeyById(@Param("id") UUID id);

}
