package com.fluxbank.wallet_service.infrastructure.handlers;

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

}
