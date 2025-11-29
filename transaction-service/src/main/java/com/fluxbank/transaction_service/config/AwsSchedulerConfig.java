package com.fluxbank.transaction_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.scheduler.SchedulerClient;

@Configuration
public class AwsSchedulerConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public SchedulerClient schedulerClient() {
        return SchedulerClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}

