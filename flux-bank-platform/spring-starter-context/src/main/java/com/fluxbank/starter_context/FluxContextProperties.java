package com.fluxbank.starter_context;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fluxbank.context")
public class FluxContextProperties {

    private boolean trustInboundIdentityHeaders = false;

    public boolean isTrustInboundIdentityHeaders() {
        return trustInboundIdentityHeaders;
    }

    public void setTrustInboundIdentityHeaders(boolean trustInboundIdentityHeaders) {
        this.trustInboundIdentityHeaders = trustInboundIdentityHeaders;
    }
}
