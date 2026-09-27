package com.fluxbank.commons_web.domain;

import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Set;

public record EndpointInfo(
        String identity,
        String controller,
        String javaMethod,
        Set<HttpMethod> httpMethods,
        Set<String> paths,
        List<String> filters,
        boolean enabled
) {
}
