package com.fluxbank.commons_web.domain;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        RequestMetadata metadata
) {}
