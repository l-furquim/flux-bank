package com.fluxbank.commons_web.domain;

public record ErrorDetails(

        String type,
        String title,
        String detail,
        Integer status,
        String correlationId

) {
}
