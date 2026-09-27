package com.fluxbank.commons_web;

import com.fluxbank.commons_web.service.EndpointRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Slf4j
@RequiredArgsConstructor
public class RoutesAutoRegistration implements ApplicationRunner {

    private final EndpointRegistry endpointRegistry;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Routes registration started");

        try {
            endpointRegistry.registerAnnotatedRoutes();
        } catch (Exception e) {
            log.error("Routes registration failed", e);
        }
    }

}
