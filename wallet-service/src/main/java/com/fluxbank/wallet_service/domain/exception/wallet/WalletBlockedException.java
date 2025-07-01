package com.fluxbank.wallet_service.domain.exception.wallet;

public class WalletBlockedException extends RuntimeException {
  public WalletBlockedException() {
    super("Your wallet is blocked, so cannot do any transaction.");
  }
}
