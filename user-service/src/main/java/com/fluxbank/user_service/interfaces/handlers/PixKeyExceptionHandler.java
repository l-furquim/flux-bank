package com.fluxbank.user_service.interfaces.handlers;

import com.fluxbank.user_service.domain.exceptions.DuplicatedKeyException;
import com.fluxbank.user_service.domain.exceptions.InvalidPixKeyCreationException;
import com.fluxbank.user_service.interfaces.handlers.generic.ExceptionHandlerResponse;
import com.fluxbank.user_service.interfaces.handlers.generic.GenericExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PixKeyExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(DuplicatedKeyException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleDuplicatedCpfKey(
            DuplicatedKeyException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidPixKeyCreationException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidPixKeyCreation(
            InvalidPixKeyCreationException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

}
