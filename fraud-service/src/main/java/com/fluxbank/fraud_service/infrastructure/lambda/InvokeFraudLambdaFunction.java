package com.fluxbank.fraud_service.infrastructure.lambda;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.fraud_service.domain.exceptions.InvokeLambdaFunctionException;
import com.fluxbank.fraud_service.domain.service.LambdaService;
import com.fluxbank.fraud_service.interfaces.dto.FraudAnalysisResponse;
import com.fluxbank.fraud_service.interfaces.dto.TransactionEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Slf4j
@Component
public class InvokeFraudLambdaFunction implements LambdaService {

    private final LambdaClient client;
    private final ObjectMapper objectMapper;

    @Value("${aws.lambda.fraud-detection}")
    private String fraudDetectionFunctionName;

    public InvokeFraudLambdaFunction(LambdaClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public FraudAnalysisResponse invoke(TransactionEventDto request){
        try {

            String jsonPayload = objectMapper.writeValueAsString(request);

            InvokeRequest invokeRequest = InvokeRequest.builder()
                    .functionName(fraudDetectionFunctionName)
                    .payload(SdkBytes.fromUtf8String(jsonPayload))
                    .build();

            InvokeResponse invokeResponse = client.invoke(invokeRequest);


            String responsePayload = invokeResponse.payload().asUtf8String();

            log.info("Payload received from lambda function: {}", responsePayload);

            JsonNode payloadNode = objectMapper.readTree(responsePayload);

            String bodyAsString = payloadNode.get("body").asText();

            return objectMapper.readValue(bodyAsString, FraudAnalysisResponse.class);

        } catch (Exception e) {
            log.error("Error invoking fraud detection lambda", e);
            throw new InvokeLambdaFunctionException("Failed to invoke fraud detection lambda: " + e.getMessage());
        }
    }

}
