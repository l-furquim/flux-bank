package com.fluxbank.wallet_service.infrastructure.persistence.repository;

import com.fluxbank.wallet_service.infrastructure.persistence.entity.PaymentMethodsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentMethodsJpaRepository extends JpaRepository<PaymentMethodsEntity, UUID> {
}
