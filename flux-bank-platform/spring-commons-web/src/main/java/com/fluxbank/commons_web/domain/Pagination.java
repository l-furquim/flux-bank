package com.fluxbank.commons_web.domain;

public record Pagination(
        Integer limit,
        Integer page,
        String offset
) {
}
