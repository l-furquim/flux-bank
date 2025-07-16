package com.fluxbank.transaction_service.client;

import com.fluxbank.transaction_service.controller.dto.ResolvePixKeyResponse;
import com.fluxbank.transaction_service.model.exceptions.UserClientUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<IUserClient> {


    @Override
    public IUserClient create(Throwable cause) {
        return new IUserClient() {
            @Override
            public ResponseEntity<ResolvePixKeyResponse> resolvePixKey(String keyValue) {
                log.error("User service unavailable with cause: {}", cause.getMessage());

                throw new UserClientUnavailableException(cause.getMessage());

                // so something....
            }
        };
    }
}
