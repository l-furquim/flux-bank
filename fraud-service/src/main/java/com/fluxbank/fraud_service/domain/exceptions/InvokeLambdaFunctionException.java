package com.fluxbank.fraud_service.domain.exceptions;

public class InvokeLambdaFunctionException extends RuntimeException {
    public InvokeLambdaFunctionException(String message) {
        super(message);
    }
}
