package com.fluxbank.commons_web.domain;

public record RequestMetadata(
        Pagination pagination,
        Sort sort
) {
}
