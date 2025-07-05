package com.fluxbank.wallet_service.infrastructure.service;

import com.fluxbank.wallet_service.application.dto.WalletUpdatedEventDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WalletEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public WalletEventService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @CircuitBreaker(name = "walletUpdatedCB", fallbackMethod = "walletUpdatedFallback")
    public void sendTransactionConfirmation(WalletUpdatedEventDto event) {
        kafkaTemplate.send("wallet.updated", event.transactionId().toString(), event);
    }

    public void walletUpdatedFallback(WalletUpdatedEventDto event, Exception e) {
        log.warn("Circuit breaker ativo - usando fallback para: {}, exceção lancada: {}", event.transactionId(), e.getMessage());

        // implementar banco de eventos nao processados
    }

}
