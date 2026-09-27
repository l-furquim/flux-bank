package com.fluxbank.commons_web.domain;

import lombok.Builder;
import org.springframework.http.HttpMethod;

import java.util.List;

@Builder(toBuilder = true)
public record Route(

        String identity,
        String service,
        String path,
        HttpMethod method,
        String uri,
        List<String> filters,
        Boolean enabled

) {
}
