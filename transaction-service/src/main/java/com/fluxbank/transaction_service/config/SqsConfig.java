package com.fluxbank.transaction_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class SqsConfig {

    @Bean
    public SqsAsyncClient amazonSQSAsync() {
        return SqsAsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

}
