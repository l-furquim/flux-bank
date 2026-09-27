package com.fluxbank.commons_core.domain;

import java.util.UUID;

public interface DomainId {

    UUID value();
    String prefix();

    default String _asString() {
        return prefix() + "_" + value().toString().replace("-", "");
    }

}
