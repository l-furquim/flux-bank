package com.fluxbank.transaction_service.repository;

import com.fluxbank.transaction_service.model.events.TransactionEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionEventRepository extends MongoRepository<TransactionEvent, UUID> {
}
