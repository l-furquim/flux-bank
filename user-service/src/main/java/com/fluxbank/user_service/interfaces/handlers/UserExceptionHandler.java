package com.fluxbank.user_service.interfaces.handlers;


import com.fluxbank.user_service.domain.exceptions.*;
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
public class UserExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(InvalidUserBirthdate.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidBirth(
            InvalidUserBirthdate ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnnauthorizedAccountCreation.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUnauthorizedAccountCreation(
            UnnauthorizedAccountCreation ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedAuthException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUnauthorizedAuth(
            UnauthorizedAuthException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CacheDataException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleCacheData(
            CacheDataException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleJwtCreation(
            TokenGenerationException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleJwtVerification(
            InvalidTokenException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }




}
