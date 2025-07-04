package com.fluxbank.wallet_service.infrastructure.service;

import com.fluxbank.wallet_service.application.dto.TransactionResult;
import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletTransactionPersistenceAdapter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WalletEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WalletTransactionPersistenceAdapter walletTransactionAdapter;


    public WalletEventService(KafkaTemplate<String, Object> kafkaTemplate, WalletTransactionPersistenceAdapter walletTransactionAdapter) {
        this.kafkaTemplate = kafkaTemplate;
        this.walletTransactionAdapter = walletTransactionAdapter;
    }

    @CircuitBreaker(name = "kafka-producer", fallbackMethod = "transactionConfirmedFallback")
    public void sendTransactionConfirmation(TransactionResult event) {
        kafkaTemplate.send("transaction.confirmed", event.transactionId().toString(), event)
                .addCallback(
                        result -> log.info("Mensagem enviada com sucesso: {}", event.getOrderId()),
                        failure -> {
                            log.error("Falha ao enviar mensagem {}", failure);
                            throw new RuntimeException("Falha no envio", failure);
                        }
                );
    }

    public void transactionConfirmedFallback(TransactionResult event, Exception e) {
        log.warn("Circuit breaker ativo - usando fallback para: {}", event.transactionId());

        // Implementar fallback (salvar no banco, cache, etc.)
        walletTransactionAdapter.updateWalletTransactionStatus(
                event.transactionId(),
                TransactionStatus.FAILED
        );
    }

}
