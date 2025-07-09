package com.fluxbank.wallet_service.infrastructure.handlers;

import com.fluxbank.wallet_service.domain.exception.walletlimit.LimitBlockedException;
import com.fluxbank.wallet_service.domain.exception.walletlimit.UnavailableLimitException;
import com.fluxbank.wallet_service.domain.exception.walletlimit.WalletLimitNotFoundException;
import com.fluxbank.wallet_service.infrastructure.handlers.dto.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WalletLimitExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(WalletLimitNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleWalletLimitNotFound(
            WalletLimitNotFoundException ex, HttpServletRequest request
    ) {
        return super.buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(LimitBlockedException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleLimitBlocked(
            LimitBlockedException ex, HttpServletRequest request
    ) {
        return super.buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnavailableLimitException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUnavailableLimit(
            UnavailableLimitException ex, HttpServletRequest request
    ) {
        return super.buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI());
    }






}
