package com.fluxbank.starter_security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fluxbank.security")
public class FluxSecurityProperties {

    private boolean enabled = true;

    private final Jwt jwt = new Jwt();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public static class Jwt {

        private String issuer;

        private String audience;

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }
    }
}
