package com.fluxbank.transaction_service.client;

import com.fluxbank.transaction_service.controller.dto.ResolvePixKeyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "userClient",
        url = "${feign-client.user-uri}"
)
public interface IUserClient {

    @GetMapping("/pix-keys/{keyValue}")
    public ResponseEntity<ResolvePixKeyResponse> resolvePixKey(
            @PathVariable("keyValue") String keyValue
    );

}
