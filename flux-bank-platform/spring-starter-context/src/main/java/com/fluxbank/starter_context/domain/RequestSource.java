package com.fluxbank.starter_context.domain;

import com.fluxbank.commons_core.domain.id.DomainIds;

import java.util.Locale;

public enum RequestSource {

    MOBILE,
    WEB,
    BACKOFFICE,
    SERVICE,
    UNKNOWN;

    public static RequestSource fromHeader(String raw) {
        if (!DomainIds.isSafeHeader(raw)) {
            return UNKNOWN;
        }
        try {
            return RequestSource.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return UNKNOWN;
        }
    }
}
