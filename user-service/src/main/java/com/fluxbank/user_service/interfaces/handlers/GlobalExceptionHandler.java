package com.fluxbank.user_service.interfaces.handlers;

import com.fluxbank.user_service.interfaces.handlers.generic.ExceptionHandlerResponse;
import com.fluxbank.user_service.interfaces.handlers.generic.GenericExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionHandlerResponse> handleGlobalErrors(
            Exception ex, HttpServletRequest request) {

        log.info("Caiu no global");

        log.error("Exceção não mapeada: {}", Arrays.toString(ex.getStackTrace()));

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, request.getRequestURI());
    }

}

