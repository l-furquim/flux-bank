package com.fluxbank.commons_web.config;

import com.fluxbank.commons_web.RoutesAutoRegistration;
import com.fluxbank.commons_web.domain.Route;
import com.fluxbank.commons_web.service.EndpointRegistry;
import com.fluxbank.commons_web.service.RouteHasher;
import com.fluxbank.commons_web.service.RouteService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
@ConditionalOnProperty(prefix = "fluxbank.routes", name = "enabled", havingValue = "true")
public class FluxRouteAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "routeRedisTemplate")
    RedisTemplate<String, Route> routeRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        RedisTemplate<String, Route> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keys = new StringRedisSerializer();
        JacksonJsonRedisSerializer<Route> values = new JacksonJsonRedisSerializer<>(objectMapper, Route.class);

        template.setKeySerializer(keys);
        template.setValueSerializer(values);
        template.setHashKeySerializer(keys);
        template.setHashValueSerializer(values);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    RouteHasher routeHasher() {
        return new RouteHasher();
    }

    @Bean
    @ConditionalOnMissingBean
    RouteService routeService(
            RedisTemplate<String, Route> routeRedisTemplate,
            StringRedisTemplate stringRedisTemplate,
            RouteHasher routeHasher
    ) {
        return new RouteService(routeRedisTemplate, stringRedisTemplate, routeHasher);
    }

    @Bean
    @ConditionalOnMissingBean
    EndpointRegistry endpointRegistry(
            List<RequestMappingHandlerMapping> handlerMappings,
            RouteService routeService
    ) {
        return new EndpointRegistry(handlerMappings, routeService);
    }

    @Bean
    @ConditionalOnMissingBean
    RoutesAutoRegistration routesAutoRegistration(EndpointRegistry endpointRegistry) {
        return new RoutesAutoRegistration(endpointRegistry);
    }
}
