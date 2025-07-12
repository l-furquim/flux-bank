package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.UpdateWalletLimitRequest;
import com.fluxbank.wallet_service.domain.enums.LimitStatus;
import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.domain.enums.TransactionType;
import com.fluxbank.wallet_service.domain.exception.walletlimit.LimitBlockedException;
import com.fluxbank.wallet_service.domain.exception.walletlimit.UnavailableLimitException;
import com.fluxbank.wallet_service.domain.exception.walletlimit.WalletLimitNotFoundException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.domain.strategy.WalletLimitResetStrategy;
import com.fluxbank.wallet_service.domain.strategy.factories.WalletLimitResetStrategyFactory;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletLimitAdapter;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WalletLimitDomainServiceTest {
    @Mock
    private WalletLimitAdapter adapter;

    @Mock
    private WalletTransactionDomainService transactionService;

    @Mock
    private WalletLimitResetStrategyFactory strategyFactory;

    @Mock
    private WalletLimitResetStrategy resetStrategy;

    @Mock
    private WalletLimit walletLimit;

    private WalletLimitDomainService service;

    private Wallet wallet;
    private UUID walletId;
    private UUID limitId;

    @BeforeEach
    void setUp() {
        service = new WalletLimitDomainService(adapter, transactionService, strategyFactory);

        walletId = UUID.randomUUID();
        limitId = UUID.randomUUID();

        wallet = Wallet.builder()
                .id(walletId)
                .build();
    }

    @Test
    @DisplayName("Deve criar limites iniciais para uma carteira")
    void shouldCreateInitialLimits() {
        // Given
        ArgumentCaptor<WalletLimit> limitCaptor = ArgumentCaptor.forClass(WalletLimit.class);
        ArgumentCaptor<WalletEntity> entityCaptor = ArgumentCaptor.forClass(WalletEntity.class);

        // When
        service.createInitialLimit(wallet);

        // Then
        verify(adapter, times(5)).create(limitCaptor.capture(), entityCaptor.capture());

        List<WalletLimit> createdLimits = limitCaptor.getAllValues();
        List<WalletEntity> createdEntities = entityCaptor.getAllValues();

        assertEquals(5, createdLimits.size());
        assertEquals(5, createdEntities.size());

        // Verifica se todos os tipos de limite foram criados
        assertTrue(createdLimits.stream().anyMatch(limit -> limit.getLimitType() == LimitType.DAILY_PIX));
        assertTrue(createdLimits.stream().anyMatch(limit -> limit.getLimitType() == LimitType.DAILY_TRANSACTION));
        assertTrue(createdLimits.stream().anyMatch(limit -> limit.getLimitType() == LimitType.SINGLE_TRANSACTION));
        assertTrue(createdLimits.stream().anyMatch(limit -> limit.getLimitType() == LimitType.MONTHLY_TRANSACTION));
        assertTrue(createdLimits.stream().anyMatch(limit -> limit.getLimitType() == LimitType.MONTHLY_PIX));

        // Verifica valores padrão
        WalletLimit dailyPixLimit = createdLimits.stream()
                .filter(limit -> limit.getLimitType() == LimitType.DAILY_PIX)
                .findFirst().orElseThrow();
        assertEquals(BigDecimal.valueOf(1500), dailyPixLimit.getLimitAmount());

        // Verifica se as entidades foram criadas corretamente
        createdEntities.forEach(entity -> assertEquals(walletId, entity.getId()));
    }

    @Test
    @DisplayName("Deve atualizar limite da carteira com sucesso")
    void shouldUpdateWalletLimitSuccessfully() {
        // Given
        BigDecimal requestAmount = BigDecimal.valueOf(500);
        UpdateWalletLimitRequest request = new UpdateWalletLimitRequest(wallet, requestAmount, TransactionType.PIX);

        when(walletLimit.getId()).thenReturn(limitId);
        when(adapter.findByWalletId(walletId)).thenReturn(List.of(walletLimit));
        when(walletLimit.hasAvailableLimit(requestAmount)).thenReturn(true);
        when(walletLimit.getStatus()).thenReturn(LimitStatus.ACTIVE);
        when(walletLimit.isLimitExceeded()).thenReturn(false);

        // When
        service.updateWalletLimit(request);

        // Then
        verify(walletLimit).subtractLimit(requestAmount);
        verify(adapter).updateWalletLimit(limitId, requestAmount, LimitStatus.ACTIVE);
    }

    @Test
    @DisplayName("Deve lançar exceção quando limite não for encontrado")
    void shouldThrowExceptionWhenLimitNotFound() {
        // Given
        UpdateWalletLimitRequest request = new UpdateWalletLimitRequest(wallet, BigDecimal.valueOf(500), TransactionType.PIX);
        when(adapter.findByWalletId(walletId)).thenReturn(List.of());

        // When & Then
        assertThrows(WalletLimitNotFoundException.class, () -> service.updateWalletLimit(request));
    }

    @Test
    @DisplayName("Deve lançar exceção quando limite estiver bloqueado")
    void shouldThrowExceptionWhenLimitIsBlocked() {
        // Given
        UpdateWalletLimitRequest request = new UpdateWalletLimitRequest(wallet, BigDecimal.valueOf(500), TransactionType.PIX);
        when(adapter.findByWalletId(walletId)).thenReturn(List.of(walletLimit));
        when(walletLimit.getStatus()).thenReturn(LimitStatus.EXCEEDED);

        // When & Then
        assertThrows(LimitBlockedException.class, () -> service.updateWalletLimit(request));
    }

    @Test
    @DisplayName("Deve lançar exceção quando limite estiver inativo")
    void shouldThrowExceptionWhenLimitIsInactive() {
        // Given
        UpdateWalletLimitRequest request = new UpdateWalletLimitRequest(wallet, BigDecimal.valueOf(500), TransactionType.PIX);
        when(adapter.findByWalletId(walletId)).thenReturn(List.of(walletLimit));
        when(walletLimit.getStatus()).thenReturn(LimitStatus.INACTIVE);

        // When & Then
        assertThrows(LimitBlockedException.class, () -> service.updateWalletLimit(request));
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há limite disponível")
    void shouldThrowExceptionWhenLimitNotAvailable() {
        // Given
        BigDecimal requestAmount = BigDecimal.valueOf(2000);
        UpdateWalletLimitRequest request = new UpdateWalletLimitRequest(wallet, requestAmount, TransactionType.PIX);

        when(walletLimit.getLimitType()).thenReturn(LimitType.DAILY_PIX);
        when(adapter.findByWalletId(walletId)).thenReturn(List.of(walletLimit));
        when(walletLimit.getStatus()).thenReturn(LimitStatus.ACTIVE);
        when(walletLimit.isLimitExceeded()).thenReturn(false);
        when(walletLimit.hasAvailableLimit(requestAmount)).thenReturn(false);

        // When & Then
        UnavailableLimitException exception = assertThrows(UnavailableLimitException.class,
                () -> service.updateWalletLimit(request));

        assertTrue(exception.getMessage().contains("daily_pix"));
    }

    @Test
    @DisplayName("Deve atualizar status para EXCEEDED quando limite for excedido")
    void shouldUpdateStatusToExceededWhenLimitExceeded() {
        // Given
        BigDecimal requestAmount = BigDecimal.valueOf(500);
        UpdateWalletLimitRequest request = new UpdateWalletLimitRequest(wallet, requestAmount, TransactionType.PIX);

        when(walletLimit.getId()).thenReturn(limitId);
        when(adapter.findByWalletId(walletId)).thenReturn(List.of(walletLimit));
        when(walletLimit.hasAvailableLimit(requestAmount)).thenReturn(true);
        when(walletLimit.getStatus()).thenReturn(LimitStatus.ACTIVE);
        when(walletLimit.isLimitExceeded()).thenReturn(false).thenReturn(true);

        // When
        service.updateWalletLimit(request);

        // Then
        verify(walletLimit).setStatus(LimitStatus.EXCEEDED);
        verify(adapter).updateWalletLimit(limitId, requestAmount, LimitStatus.EXCEEDED);
    }

    @Test
    @DisplayName("Deve resetar limite da carteira com sucesso")
    void shouldResetWalletLimitSuccessfully() {
        // Given
        BigDecimal newLimitAmount = BigDecimal.valueOf(2000);
        List<WalletTransaction> transactions = List.of(mock(WalletTransaction.class));

        when(walletLimit.getId()).thenReturn(limitId);
        when(walletLimit.getLimitType()).thenReturn(LimitType.DAILY_PIX);
        when(walletLimit.getWallet()).thenReturn(wallet);
        when(adapter.findById(limitId)).thenReturn(Optional.of(walletLimit));
        when(walletLimit.getStatus()).thenReturn(LimitStatus.ACTIVE);
        when(strategyFactory.getStrategy(LimitType.DAILY_PIX)).thenReturn(resetStrategy);
        when(resetStrategy.getUsageWindowDays()).thenReturn(1);
        when(transactionService.getWalletTransactionsByTypesAndWallet(
                eq(walletId),
                eq(List.of(TransactionType.PIX)),
                any(LocalDateTime.class)))
                .thenReturn(transactions);
        when(resetStrategy.calculateNewLimit(walletLimit, transactions)).thenReturn(newLimitAmount);

        // When
        service.resetWalletLimit(limitId);

        // Then
        verify(walletLimit).setLimitAmount(newLimitAmount);
        verify(walletLimit).setStatus(LimitStatus.ACTIVE);
        verify(adapter).updateWalletLimit(limitId, newLimitAmount, LimitStatus.ACTIVE);
    }

    @Test
    @DisplayName("Deve lançar exceção quando limite não for encontrado para reset")
    void shouldThrowExceptionWhenLimitNotFoundForReset() {
        // Given
        when(adapter.findById(limitId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(WalletLimitNotFoundException.class, () -> service.resetWalletLimit(limitId));
    }

    @Test
    @DisplayName("Deve retornar sem fazer nada quando limite está inativo no reset")
    void shouldReturnEarlyWhenLimitIsInactiveForReset() {
        // Given
        when(adapter.findById(limitId)).thenReturn(Optional.of(walletLimit));
        when(walletLimit.getStatus()).thenReturn(LimitStatus.INACTIVE);

        // When
        service.resetWalletLimit(limitId);

        // Then
        verify(strategyFactory, never()).getStrategy(any());
        verify(adapter, never()).updateWalletLimit(any(), any(), any());
    }

    @Test
    @DisplayName("Deve retornar sem fazer nada quando estratégia não for encontrada")
    void shouldReturnEarlyWhenStrategyNotFound() {
        // Given
        when(adapter.findById(limitId)).thenReturn(Optional.of(walletLimit));
        when(walletLimit.getStatus()).thenReturn(LimitStatus.ACTIVE);
        when(walletLimit.getLimitType()).thenReturn(LimitType.DAILY_PIX);
        when(strategyFactory.getStrategy(LimitType.DAILY_PIX)).thenReturn(null);

        // When
        service.resetWalletLimit(limitId);

        // Then
        verify(transactionService, never()).getWalletTransactionsByTypesAndWallet(any(), any(), any());
        verify(adapter, never()).updateWalletLimit(any(), any(), any());
    }

    @Test
    @DisplayName("Deve filtrar limites aplicáveis corretamente para PIX")
    void shouldFilterApplicableLimitsForPix() {
        // Given
        WalletLimit dailyPixLimit = createLimit(LimitType.DAILY_PIX);
        WalletLimit dailyTransactionLimit = createLimit(LimitType.DAILY_TRANSACTION);
        WalletLimit singleTransactionLimit = createLimit(LimitType.SINGLE_TRANSACTION);

        List<WalletLimit> limits = List.of(dailyPixLimit, dailyTransactionLimit, singleTransactionLimit);

        UpdateWalletLimitRequest request = new UpdateWalletLimitRequest(wallet, BigDecimal.valueOf(500), TransactionType.PIX);

        when(adapter.findByWalletId(walletId)).thenReturn(limits);
        when(dailyPixLimit.getStatus()).thenReturn(LimitStatus.ACTIVE);
        when(dailyTransactionLimit.getStatus()).thenReturn(LimitStatus.ACTIVE);
        when(singleTransactionLimit.getStatus()).thenReturn(LimitStatus.ACTIVE);
        when(dailyPixLimit.isLimitExceeded()).thenReturn(false);
        when(dailyTransactionLimit.isLimitExceeded()).thenReturn(false);
        when(singleTransactionLimit.isLimitExceeded()).thenReturn(false);
        when(dailyPixLimit.hasAvailableLimit(any())).thenReturn(true);
        when(dailyTransactionLimit.hasAvailableLimit(any())).thenReturn(true);
        when(singleTransactionLimit.hasAvailableLimit(any())).thenReturn(true);

        // When
        service.updateWalletLimit(request);

        // Then - deve processar dailyPixLimit, dailyTransactionLimit e singleTransactionLimit
        verify(dailyPixLimit).subtractLimit(BigDecimal.valueOf(500));
        verify(dailyTransactionLimit).subtractLimit(BigDecimal.valueOf(500));
        verify(singleTransactionLimit).subtractLimit(BigDecimal.valueOf(500));
    }

    @Test
    @DisplayName("Deve filtrar limites aplicáveis corretamente para DEBIT")
    void shouldFilterApplicableLimitsForDebit() {
        // Given
        WalletLimit dailyPixLimit = createLimit(LimitType.DAILY_PIX);
        WalletLimit dailyTransactionLimit = createLimit(LimitType.DAILY_TRANSACTION);

        List<WalletLimit> limits = List.of(dailyPixLimit, dailyTransactionLimit);

        UpdateWalletLimitRequest request = new UpdateWalletLimitRequest(wallet, BigDecimal.valueOf(500), TransactionType.DEBIT);

        when(adapter.findByWalletId(walletId)).thenReturn(limits);
        when(dailyTransactionLimit.getStatus()).thenReturn(LimitStatus.ACTIVE);
        when(dailyTransactionLimit.isLimitExceeded()).thenReturn(false);
        when(dailyTransactionLimit.hasAvailableLimit(any())).thenReturn(true);

        // When
        service.updateWalletLimit(request);

        // Then - deve processar apenas dailyTransactionLimit
        verify(dailyTransactionLimit).subtractLimit(BigDecimal.valueOf(500));
        verify(dailyPixLimit, never()).subtractLimit(any());
    }

    private WalletLimit createLimit(LimitType limitType) {
        WalletLimit limit = mock(WalletLimit.class);
        when(limit.getLimitType()).thenReturn(limitType);
        when(limit.getId()).thenReturn(UUID.randomUUID());
        return limit;
    }
}