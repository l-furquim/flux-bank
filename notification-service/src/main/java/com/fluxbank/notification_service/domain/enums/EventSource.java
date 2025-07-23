package com.fluxbank.notification_service.domain.enums;

public enum EventSource {
    TRANSACTION_SERVICE("transaction-service"),
    WALLET_SERVICE("wallet-service"),
    PIX_SERVICE("pix-service"),
    LIMIT_SERVICE("limit-service"),
    SYSTEM("system");
    
    private final String serviceName;
    
    EventSource(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public static EventSource fromTopicArn(String topicArn) {
        if (topicArn == null) return SYSTEM;
        
        if (topicArn.contains("transaction-completed") || topicArn.contains("transaction-failed")) {
            return TRANSACTION_SERVICE;
        } else if (topicArn.contains("pix-key-created")) {
            return PIX_SERVICE;
        } else if (topicArn.contains("limit-exceeded")) {
            return LIMIT_SERVICE;
        }
        
        return SYSTEM;
    }
}