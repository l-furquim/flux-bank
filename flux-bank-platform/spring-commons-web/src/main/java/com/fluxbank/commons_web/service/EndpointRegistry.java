package com.fluxbank.commons_web.service;

import com.fluxbank.commons_web.annotations.Route;
import com.fluxbank.commons_web.domain.EndpointInfo;
import com.fluxbank.commons_web.enums.RouteFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class EndpointRegistry {

    private static final Pattern DUPLICATE_SLASHES = Pattern.compile("/+");

    private static final Set<HttpMethod> DEFAULT_HTTP_METHODS = Set.of(
            HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE
    );

    private final List<RequestMappingHandlerMapping> handlerMappings;
    private final RouteService routeService;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Value("${route.internal:false}")
    private Boolean internalService;

    @Value("${server.port}")
    private String serverPort;

    public void registerAnnotatedRoutes() {
        List<com.fluxbank.commons_web.domain.Route> routes = discoverRoutes();
        routeService.syncServiceRoutes(applicationName, routes);
        log.info("Synchronized {} annotated routes for service {}", routes.size(), applicationName);
    }

    public List<com.fluxbank.commons_web.domain.Route> discoverRoutes() {
        String uri = resolveUri();
        String context = contextPath == null ? "" : contextPath;

        return handlerMappings.stream()
                .flatMap(mapping -> mapping.getHandlerMethods().entrySet().stream())
                .flatMap(entry -> toRoutes(entry.getKey(), entry.getValue(), context, uri).stream())
                .toList();
    }

    public List<EndpointInfo> getEndpoints() {
        return handlerMappings.stream()
                .flatMap(mapping -> mapping.getHandlerMethods().entrySet().stream())
                .map(entry -> toEndpointInfo(entry.getKey(), entry.getValue()))
                .flatMap(Optional::stream)
                .toList();
    }

    private List<com.fluxbank.commons_web.domain.Route> toRoutes(
            RequestMappingInfo info,
            HandlerMethod handler,
            String context,
            String uri
    ) {
        Route annotation = findRouteAnnotation(handler);
        if (annotation == null) {
            return List.of();
        }

        EndpointInfo endpoint = toEndpointInfo(info, handler, annotation);
        List<com.fluxbank.commons_web.domain.Route> routes = new ArrayList<>();

        for (String path : endpoint.paths()) {
            for (HttpMethod httpMethod : endpoint.httpMethods()) {
                routes.add(com.fluxbank.commons_web.domain.Route.builder()
                        .identity(endpoint.identity())
                        .service(applicationName)
                        .path(buildFullPath(context, path))
                        .method(httpMethod)
                        .uri(uri)
                        .filters(endpoint.filters())
                        .enabled(endpoint.enabled())
                        .build());
            }
        }

        return routes;
    }

    private Optional<EndpointInfo> toEndpointInfo(RequestMappingInfo info, HandlerMethod handler) {
        Route annotation = findRouteAnnotation(handler);
        if (annotation == null) {
            return Optional.empty();
        }
        return Optional.of(toEndpointInfo(info, handler, annotation));
    }

    private EndpointInfo toEndpointInfo(RequestMappingInfo info, HandlerMethod handler, Route annotation) {
        Class<?> controllerClass = ClassUtils.getUserClass(handler.getBeanType());
        Method method = handler.getMethod();

        return new EndpointInfo(
                identityOf(controllerClass, method),
                controllerClass.getName(),
                method.getName(),
                httpMethodsOf(info),
                info.getPatternValues(),
                toFilterNames(annotation.filters()),
                annotation.enabled()
        );
    }

    private Route findRouteAnnotation(HandlerMethod handler) {
        Route annotation = handler.getMethodAnnotation(Route.class);
        if (annotation != null) {
            return annotation;
        }
        return AnnotatedElementUtils.findMergedAnnotation(handler.getMethod(), Route.class);
    }

    private Set<HttpMethod> httpMethodsOf(RequestMappingInfo info) {
        Set<HttpMethod> methods = info.getMethodsCondition().getMethods().stream()
                .map(requestMethod -> HttpMethod.valueOf(requestMethod.name()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return methods.isEmpty() ? DEFAULT_HTTP_METHODS : methods;
    }

    private List<String> toFilterNames(RouteFilter[] filters) {
        return Arrays.stream(filters).map(Enum::name).toList();
    }

    // development only
    private String resolveUri() {
        if (internalService) {
            return "http://localhost:" + serverPort;
        }
        return "lb://" + applicationName;
    }

    private String buildFullPath(String context, String path) {
        String fullPath = DUPLICATE_SLASHES.matcher("/" + context + "/" + path).replaceAll("/");
        return fullPath.length() > 1 && fullPath.endsWith("/")
                ? fullPath.substring(0, fullPath.length() - 1)
                : fullPath;
    }

    private String identityOf(Class<?> controllerClass, Method method) {
        String params = Arrays.stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(Collectors.joining(","));
        return controllerClass.getName() + "#" + method.getName() + "(" + params + ")";
    }

}
