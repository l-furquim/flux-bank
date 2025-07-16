package com.fluxbank.transaction_service.model.exceptions;

public class UserClientUnavailableException extends RuntimeException {
  public UserClientUnavailableException(String message) {
    super(message);
  }
}
