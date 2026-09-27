package com.fluxbank.starter_context.domain;

public enum FluxHeaders {

    X_REQUEST_ID("X-Request-Id"),
    X_CORRELATION_ID("X-Correlation-Id"),
    X_TENANT_ID("X-Tenant-Id"),
    X_ACCOUNT_ID("X-Account-Id"),
    X_USER_ID("X-User-Id"),
    X_DEVICE_ID("X-Device-Id"),
    X_SESSION_ID("X-Session-Id"),
    X_CLIENT_ID("X-Client-Id"),
    X_SOURCE("X-Source");

    private final String value;

    FluxHeaders(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
