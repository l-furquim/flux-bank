package com.fluxbank.transaction_service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "walletClient", url = "${feign-client.wallet-uri}")
public interface IWalletClient {



}
