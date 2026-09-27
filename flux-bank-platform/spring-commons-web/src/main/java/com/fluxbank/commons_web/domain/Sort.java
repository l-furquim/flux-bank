package com.fluxbank.commons_web.domain;

import com.fluxbank.commons_web.enums.Order;

public record Sort(
        String field,
        Order order
) {
}
