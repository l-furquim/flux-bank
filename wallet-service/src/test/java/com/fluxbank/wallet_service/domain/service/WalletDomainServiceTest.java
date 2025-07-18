package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.*;
import com.fluxbank.wallet_service.application.port.WalletLimitPort;
import com.fluxbank.wallet_service.application.port.WalletTransactionPort;
import com.fluxbank.wallet_service.domain.enums.*;
import com.fluxbank.wallet_service.domain.exception.wallet.*;
import com.fluxbank.wallet_service.domain.exception.wallettransaction.InvalidWalletRefundException;
import com.fluxbank.wallet_service.domain.exception.wallettransaction.WalletTransactionNotFoundException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletPersistenceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletDomainServiceTest {

    @Mock
    private WalletPersistenceAdapter adapter;

    @Mock
    private WalletTransactionPort walletTransactionService;

    @Mock
    private WalletLimitPort walletLimitService;

    @InjectMocks
    private WalletDomainService walletDomainService;

    private UUID userId;
    private UUID walletId;
    private UUID transactionId;
    private Wallet wallet;
    private WalletTransaction walletTransaction;
    private WalletLimit walletLimit;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        walletId = UUID.randomUUID();
        transactionId = UUID.randomUUID();

        wallet = Wallet.builder()
                .id(walletId)
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .currency(Currency.BRL)
                .walletStatus(WalletStatus.ACTIVE)
                .blockedAmount(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        walletTransaction = WalletTransaction.builder()
                .id(UUID.randomUUID())
                .wallet(wallet)
                .transactionId(transactionId)
                .transactionType(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(100))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1100))
                .description("Test transaction")
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        walletLimit = WalletLimit.builder()
                .id(UUID.randomUUID())
                .wallet(wallet)
                .limitType(LimitType.DAILY_TRANSACTION)
                .limitAmount(BigDecimal.valueOf(5000))
                .usedAmount(BigDecimal.valueOf(1000))
                .status(LimitStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create wallet successfully when user doesn't have wallet with same currency")
    void shouldCreateWalletSuccessfully() {
        CreateWalletRequest request = new CreateWalletRequest("BRL");
        when(adapter.findWalletsByUserId(userId)).thenReturn(List.of());
        when(adapter.saveWallet(any(Wallet.class))).thenReturn(wallet);

        Wallet result = walletDomainService.createWallet(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.getCurrency()).isEqualTo(Currency.BRL);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getWalletStatus()).isEqualTo(WalletStatus.ACTIVE);

        verify(adapter).findWalletsByUserId(userId);
        verify(adapter).saveWallet(any(Wallet.class));
        verify(walletLimitService).createInitialLimit(wallet);
    }

    @Test
    @DisplayName("Should throw exception when user already has wallet with same currency")
    void shouldThrowExceptionWhenUserAlreadyHasWalletWithSameCurrency() {
        CreateWalletRequest request = new CreateWalletRequest("BRL");
        when(adapter.findWalletsByUserId(userId)).thenReturn(List.of(wallet));

        assertThatThrownBy(() -> walletDomainService.createWallet(request, userId))
                .isInstanceOf(DuplicatedWalletCurrencyException.class)
                .hasMessage("Cannot have two wallets with the same currency");

        verify(adapter).findWalletsByUserId(userId);
        verify(adapter, never()).saveWallet(any(Wallet.class));
        verify(walletLimitService, never()).createInitialLimit(any(Wallet.class));
    }

    @Test
    @DisplayName("Should deposit successfully when wallet exists")
    void shouldDepositSuccessfully() {
        DepositInWalletRequest request = new DepositInWalletRequest(
                transactionId.toString(),
                BigDecimal.valueOf(100),
                userId.toString(),
                TransactionType.CREDIT,
                "{\"key\":\"value\"}",
                "Test deposit",
                Currency.BRL
                );

        when(adapter.findWalletsByUserId(userId)).thenReturn(List.of(wallet));
        when(walletTransactionService.create(any(CreateWalletTransactionDto.class)))
                .thenReturn(walletTransaction);

        TransactionResult result = walletDomainService.deposit(request);

        assertThat(result).isNotNull();
        assertThat(result.transactionId()).isEqualTo(transactionId);
        assertThat(result.type()).isEqualTo(TransactionType.CREDIT);
        assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(result.currency()).isEqualTo(Currency.BRL);

        verify(adapter).findWalletsByUserId(userId);
        verify(walletTransactionService).create(any(CreateWalletTransactionDto.class));
        verify(adapter).updateWalletBalance(any(BigDecimal.class), eq(wallet.getId()));
    }

    @Test
    @DisplayName("Should throw exception when wallet not found for deposit")
    void shouldThrowExceptionWhenWalletNotFoundForDeposit() {
        DepositInWalletRequest request = new DepositInWalletRequest(
                transactionId.toString(),
                BigDecimal.valueOf(100),
                userId.toString(),
                TransactionType.CREDIT,
                "Test deposit",
                "{\"key\":\"value\"}",
                Currency.USD
                );

        when(adapter.findWalletsByUserId(userId)).thenReturn(List.of(wallet));

        assertThatThrownBy(() -> walletDomainService.deposit(request))
                .isInstanceOf(WalletNotFoundException.class);

        verify(adapter).findWalletsByUserId(userId);
        verify(walletTransactionService, never()).create(any(CreateWalletTransactionDto.class));
        verify(adapter, never()).updateWalletBalance(any(BigDecimal.class), any(UUID.class));
    }

    @Test
    @DisplayName("Should return balance successfully when wallet exists and user is authorized")
    void shouldReturnBalanceSuccessfully() {
        GetWalletBalanceRequest request = new GetWalletBalanceRequest(walletId.toString());
        when(adapter.findWalletById(walletId)).thenReturn(wallet);

        GetWalletBalanceResponse result = walletDomainService.balance(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.balance()).isEqualTo(wallet.getBalance());
        assertThat(result.currency()).isEqualTo(wallet.getCurrency());
        assertThat(result.blockedAmount()).isEqualTo(wallet.getBlockedAmount());
        assertThat(result.status()).isEqualTo(wallet.getWalletStatus());

        verify(adapter).findWalletById(walletId);
    }

    @Test
    @DisplayName("Should throw exception when wallet not found for balance")
    void shouldThrowExceptionWhenWalletNotFoundForBalance() {
        GetWalletBalanceRequest request = new GetWalletBalanceRequest(walletId.toString());
        when(adapter.findWalletById(walletId)).thenReturn(null);

        assertThatThrownBy(() -> walletDomainService.balance(request, userId))
                .isInstanceOf(WalletNotFoundException.class);

        verify(adapter).findWalletById(walletId);
    }

    @Test
    @DisplayName("Should throw exception when user is not authorized for balance")
    void shouldThrowExceptionWhenUserNotAuthorizedForBalance() {
        GetWalletBalanceRequest request = new GetWalletBalanceRequest(walletId.toString());
        UUID differentUserId = UUID.randomUUID();
        when(adapter.findWalletById(walletId)).thenReturn(wallet);

        assertThatThrownBy(() -> walletDomainService.balance(request, differentUserId))
                .isInstanceOf(UnnauthorizedBalanceRequestException.class);

        verify(adapter).findWalletById(walletId);
    }

    @Test
    @DisplayName("Should withdraw successfully when conditions are met")
    void shouldWithdrawSuccessfully() {
        WithDrawRequest request = new WithDrawRequest(
                userId.toString(),
                BigDecimal.valueOf(100),
                transactionId,
                TransactionType.DEBIT,
                "{\"key\":\"value\"}",
                Currency.BRL
                );

        when(adapter.findWalletsByUserId(userId)).thenReturn(List.of(wallet));
        when(walletTransactionService.create(any(CreateWalletTransactionDto.class)))
                .thenReturn(walletTransaction);

        TransactionResult result = walletDomainService.withDraw(request, userId.toString());

        assertThat(result).isNotNull();
        assertThat(result.transactionId()).isEqualTo(transactionId);
        assertThat(result.type()).isEqualTo(TransactionType.DEBIT);
        assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(result.currency()).isEqualTo(Currency.BRL);

        verify(adapter).findWalletsByUserId(userId);
        verify(walletLimitService).updateWalletLimit(any(UpdateWalletLimitRequest.class));
        verify(walletTransactionService).create(any(CreateWalletTransactionDto.class));
        verify(adapter).updateWalletBalance(any(BigDecimal.class), eq(wallet.getId()));
    }

    @Test
    @DisplayName("Should throw exception when wallet not found for withdraw")
    void shouldThrowExceptionWhenWalletNotFoundForWithdraw() {
        WithDrawRequest request = new WithDrawRequest(
                userId.toString(),
                BigDecimal.valueOf(100),
                transactionId,
                TransactionType.DEBIT,
                "{\"key\":\"value\"}",
                Currency.USD
                );

        when(adapter.findWalletsByUserId(userId)).thenReturn(List.of(wallet));

        assertThatThrownBy(() -> walletDomainService.withDraw(request, userId.toString()))
                .isInstanceOf(WalletNotFoundException.class);

        verify(adapter).findWalletsByUserId(userId);
        verify(walletLimitService, never()).updateWalletLimit(any(UpdateWalletLimitRequest.class));
        verify(walletTransactionService, never()).create(any(CreateWalletTransactionDto.class));
    }

    @Test
    @DisplayName("Should throw exception when user is not authorized for withdraw")
    void shouldThrowExceptionWhenUserNotAuthorizedForWithdraw() {
        WithDrawRequest request = new WithDrawRequest(
                userId.toString(),
                BigDecimal.valueOf(100),
                transactionId,
                TransactionType.DEBIT,
                "{\"key\":\"value\"}",
                Currency.BRL
                );

        UUID differentUserId = UUID.randomUUID();
        when(adapter.findWalletsByUserId(userId)).thenReturn(List.of(wallet));

        assertThatThrownBy(() -> walletDomainService.withDraw(request, differentUserId.toString()))
                .isInstanceOf(UnauthorizedWithDrawRequest.class);

        verify(adapter).findWalletsByUserId(userId);
        verify(walletLimitService, never()).updateWalletLimit(any(UpdateWalletLimitRequest.class));
        verify(walletTransactionService, never()).create(any(CreateWalletTransactionDto.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient balance for withdraw")
    void shouldThrowExceptionWhenInsufficientBalanceForWithdraw() {
        WithDrawRequest request = new WithDrawRequest(
                userId.toString(),
                BigDecimal.valueOf(2000),
                transactionId,
                TransactionType.DEBIT,
                "{\"key\":\"value\"}",
                Currency.BRL
                );

        when(adapter.findWalletsByUserId(userId)).thenReturn(List.of(wallet));

        assertThatThrownBy(() -> walletDomainService.withDraw(request, userId.toString()))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(adapter).findWalletsByUserId(userId);
        verify(walletLimitService, never()).updateWalletLimit(any(UpdateWalletLimitRequest.class));
        verify(walletTransactionService, never()).create(any(CreateWalletTransactionDto.class));
    }

    @Test
    @DisplayName("Should return limits successfully when wallet exists and user is authorized")
    void shouldReturnLimitsSuccessfully() {
        GetWalletLimitsRequest request = new GetWalletLimitsRequest(walletId.toString());
        when(adapter.findWalletById(walletId)).thenReturn(wallet);
        when(walletLimitService.findByWallet(wallet)).thenReturn(List.of(walletLimit));

        GetWalletLimitsResponse result = walletDomainService.getLimits(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.limits()).hasSize(1);
        assertThat(result.limits().get(0).type()).isEqualTo(LimitType.DAILY_TRANSACTION);
        assertThat(result.limits().get(0).amount()).isEqualTo(BigDecimal.valueOf(5000));
        assertThat(result.limits().get(0).used()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(result.limits().get(0).status()).isEqualTo(LimitStatus.ACTIVE);

        verify(adapter).findWalletById(walletId);
        verify(walletLimitService).findByWallet(wallet);
    }

    @Test
    @DisplayName("Should throw exception when wallet not found for limits")
    void shouldThrowExceptionWhenWalletNotFoundForLimits() {
        GetWalletLimitsRequest request = new GetWalletLimitsRequest(walletId.toString());
        when(adapter.findWalletById(walletId)).thenReturn(null);

        assertThatThrownBy(() -> walletDomainService.getLimits(request, userId))
                .isInstanceOf(WalletNotFoundException.class);

        verify(adapter).findWalletById(walletId);
        verify(walletLimitService, never()).findByWallet(any(Wallet.class));
    }

    @Test
    @DisplayName("Should throw exception when user is not authorized for limits")
    void shouldThrowExceptionWhenUserNotAuthorizedForLimits() {
        GetWalletLimitsRequest request = new GetWalletLimitsRequest(walletId.toString());
        UUID differentUserId = UUID.randomUUID();
        when(adapter.findWalletById(walletId)).thenReturn(wallet);

        assertThatThrownBy(() -> walletDomainService.getLimits(request, differentUserId))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("Unauthorized operation.");

        verify(adapter).findWalletById(walletId);
        verify(walletLimitService, never()).findByWallet(any(Wallet.class));
    }

    @Test
    @DisplayName("Should refund successfully when conditions are met")
    void shouldRefundSuccessfully() {
        UUID payeeId = UUID.randomUUID();
        UUID payerWalletId = UUID.randomUUID();

        Wallet payerWallet = Wallet.builder()
                .id(payerWalletId)
                .userId(UUID.randomUUID())
                .balance(BigDecimal.valueOf(900))
                .currency(Currency.BRL)
                .walletStatus(WalletStatus.ACTIVE)
                .blockedAmount(BigDecimal.ZERO)
                .build();

        Wallet payeeWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(payeeId)
                .balance(BigDecimal.valueOf(1100))
                .currency(Currency.BRL)
                .walletStatus(WalletStatus.ACTIVE)
                .blockedAmount(BigDecimal.ZERO)
                .build();

        WalletTransaction transactionToRefund = WalletTransaction.builder()
                .id(UUID.randomUUID())
                .wallet(payerWallet)
                .transactionId(transactionId)
                .transactionType(TransactionType.DEBIT)
                .amount(BigDecimal.valueOf(100))
                .status(TransactionStatus.COMPLETED)
                .build();

        RefundWalletTransactionRequest request = new RefundWalletTransactionRequest(
                transactionToRefund.getId().toString(),
                payeeId.toString()
        );

        when(walletTransactionService.findById(transactionToRefund.getId())).thenReturn(transactionToRefund);
        when(adapter.findWalletById(payerWalletId)).thenReturn(payerWallet);
        when(adapter.findWalletsByUserId(payeeId)).thenReturn(List.of(payeeWallet));

        walletDomainService.refund(request);

        verify(walletTransactionService).findById(transactionToRefund.getId());
        verify(adapter).findWalletById(payerWalletId);
        verify(adapter).findWalletsByUserId(payeeId);
        verify(walletLimitService).rollbackWalletLimit(payerWallet, transactionToRefund);
        verify(walletTransactionService).createRefundTransaction(transactionToRefund, payeeWallet);
        verify(adapter).updateWallet(payerWallet);
        verify(adapter).updateWallet(payeeWallet);
    }

    @Test
    @DisplayName("Should throw exception when refund data is invalid")
    void shouldThrowExceptionWhenRefundDataIsInvalid() {
        RefundWalletTransactionRequest request = new RefundWalletTransactionRequest(
                "invalid-uuid",
                "invalid-uuid"
        );

        assertThatThrownBy(() -> walletDomainService.refund(request))
                .isInstanceOf(InvalidWalletRefundException.class)
                .hasMessage("Invalid data for refunding the wallet.");

        verify(walletTransactionService, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception when transaction not found for refund")
    void shouldThrowExceptionWhenTransactionNotFoundForRefund() {
        UUID walletTransactionId = UUID.randomUUID();
        UUID payeeId = UUID.randomUUID();

        RefundWalletTransactionRequest request = new RefundWalletTransactionRequest(
                walletTransactionId.toString(),
                payeeId.toString()
        );

        when(walletTransactionService.findById(walletTransactionId)).thenReturn(null);

        assertThatThrownBy(() -> walletDomainService.refund(request))
                .isInstanceOf(WalletTransactionNotFoundException.class)
                .hasMessage("Could not found the transaction.");

        verify(walletTransactionService).findById(walletTransactionId);
        verify(adapter, never()).findWalletById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception when payee wallet not found for refund")
    void shouldThrowExceptionWhenPayeeWalletNotFoundForRefund() {
        UUID payeeId = UUID.randomUUID();
        UUID payerWalletId = UUID.randomUUID();

        Wallet payerWallet = Wallet.builder()
                .id(payerWalletId)
                .userId(UUID.randomUUID())
                .balance(BigDecimal.valueOf(900))
                .currency(Currency.BRL)
                .walletStatus(WalletStatus.ACTIVE)
                .blockedAmount(BigDecimal.ZERO)
                .build();

        WalletTransaction transactionToRefund = WalletTransaction.builder()
                .id(UUID.randomUUID())
                .wallet(payerWallet)
                .transactionId(transactionId)
                .transactionType(TransactionType.DEBIT)
                .amount(BigDecimal.valueOf(100))
                .status(TransactionStatus.COMPLETED)
                .build();

        RefundWalletTransactionRequest request = new RefundWalletTransactionRequest(
                transactionToRefund.getId().toString(),
                payeeId.toString()
        );

        when(walletTransactionService.findById(transactionToRefund.getId())).thenReturn(transactionToRefund);
        when(adapter.findWalletById(payerWalletId)).thenReturn(payerWallet);
        when(adapter.findWalletsByUserId(payeeId)).thenReturn(List.of());

        assertThatThrownBy(() -> walletDomainService.refund(request))
                .isInstanceOf(WalletNotFoundException.class);

        verify(walletTransactionService).findById(transactionToRefund.getId());
        verify(adapter).findWalletById(payerWalletId);
        verify(adapter).findWalletsByUserId(payeeId);
        verify(walletLimitService, never()).rollbackWalletLimit(any(Wallet.class), any(WalletTransaction.class));
    }

    @Test
    @DisplayName("Should throw exception when wallet is closed")
    void shouldThrowExceptionWhenWalletIsClosed() {
        Wallet closedWallet = Wallet.builder()
                .id(walletId)
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .currency(Currency.BRL)
                .walletStatus(WalletStatus.CLOSED)
                .blockedAmount(BigDecimal.ZERO)
                .build();

        GetWalletLimitsRequest request = new GetWalletLimitsRequest(walletId.toString());
        when(adapter.findWalletById(walletId)).thenReturn(closedWallet);

        assertThatThrownBy(() -> walletDomainService.getLimits(request, userId))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("The wallet is current closed.");

        verify(adapter).findWalletById(walletId);
        verify(walletLimitService, never()).findByWallet(any(Wallet.class));
    }

    @Test
    @DisplayName("Should throw exception when wallet is not allowed to use")
    void shouldThrowExceptionWhenWalletIsNotAllowedToUse() {
        Wallet blockedWallet = Wallet.builder()
                .id(walletId)
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .currency(Currency.BRL)
                .walletStatus(WalletStatus.BLOCKED)
                .blockedAmount(BigDecimal.ZERO)
                .build();

        GetWalletLimitsRequest request = new GetWalletLimitsRequest(walletId.toString());
        when(adapter.findWalletById(walletId)).thenReturn(blockedWallet);

        assertThatThrownBy(() -> walletDomainService.getLimits(request, userId))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("This wallet is blocked or suspend, please verify the wallet status and see what you can do.");

        verify(adapter).findWalletById(walletId);
        verify(walletLimitService, never()).findByWallet(any(Wallet.class));
    }
}