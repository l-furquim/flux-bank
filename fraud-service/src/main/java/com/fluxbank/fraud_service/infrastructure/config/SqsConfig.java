package com.fluxbank.fraud_service.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class SqsConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    public SqsAsyncClient amazonSQSAsync() {
        return SqsAsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

}
