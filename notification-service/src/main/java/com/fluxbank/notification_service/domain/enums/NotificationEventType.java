package com.fluxbank.notification_service.domain.enums;

public enum NotificationEventType {

    PIX_SENT("PIX_SENT", "pix_sent_template.html"),
    PIX_RECEIVED("PIX_RECEIVED", "pix_received_template.html"),
    CARD_TRANSACTION_COMPLETED("CARD_TRANSACTION_COMPLETED", "card_transaction_template.html"),
    DEPOSIT_COMPLETED("DEPOSIT_COMPLETED", "deposit_completed_template.html"),
    WITHDRAWAL_COMPLETED("WITHDRAWAL_COMPLETED", "withdrawal_completed_template.html"),
    
    PIX_SENT_FAILED("PIX_SENT_FAILED", "pix_failed_template.html"),
    PIX_RECEIVED_FAILED("PIX_RECEIVED_FAILED", "pix_failed_template.html"),
    TRANSACTION_FAILED("TRANSACTION_FAILED", "transaction_failed_template.html"),
    
    PIX_KEY_CREATED("PIX_KEY_CREATED", "pix_key_created_template.html"),
    LIMIT_EXCEEDED("LIMIT_EXCEEDED", "limit_exceeded_template.html"),
    
    // Fallback
    UNKNOWN("UNKNOWN", "generic_notification_template.html");
    
    private final String eventType;
    private final String templateName;
    
    NotificationEventType(String eventType, String templateName) {
        this.eventType = eventType;
        this.templateName = templateName;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public static NotificationEventType fromEventTypeAndTransactionType(String eventType, String transactionType, String status) {
        if ("COMPLETED".equals(status)) {
            return switch (eventType) {
                case "SENT" -> "PIX".equals(transactionType) ? PIX_SENT : TRANSACTION_COMPLETED;
                case "RECEIVED" -> "PIX".equals(transactionType) ? PIX_RECEIVED : TRANSACTION_COMPLETED;
                case "PIX_KEY_CREATED" -> PIX_KEY_CREATED;
                case "LIMIT_EXCEEDED" -> LIMIT_EXCEEDED;
                default -> UNKNOWN;
            };
        }
        if ("FAILED".equals(status)) {
            return switch (eventType) {
                case "SENT" -> "PIX".equals(transactionType) ? PIX_SENT_FAILED : TRANSACTION_FAILED;
                case "RECEIVED" -> "PIX".equals(transactionType) ? PIX_RECEIVED_FAILED : TRANSACTION_FAILED;
                default -> TRANSACTION_FAILED;
            };
        }
        
        return UNKNOWN;
    }
    
    public static final NotificationEventType TRANSACTION_COMPLETED = CARD_TRANSACTION_COMPLETED;
}