package com.fluxbank.wallet_service.infrastructure.handlers;

import com.fluxbank.wallet_service.infrastructure.handlers.dto.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class SqlExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleSqlException(
            SQLException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }
}
