package com.fluxbank.user_service.infrastructure.persistence;

import com.fluxbank.user_service.infrastructure.persistence.entity.UserDeviceEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDeviceJpaRepository extends JpaRepository<UserDeviceEntity, UUID> {

    @Query("SELECT d FROM UserDeviceEntity d WHERE d.userId = :userId")
    List<UserDeviceEntity> findByUserId(@Param("userId") UUID userId);

}
