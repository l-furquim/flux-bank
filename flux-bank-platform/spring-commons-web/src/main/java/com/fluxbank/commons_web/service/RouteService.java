package com.fluxbank.commons_web.service;

import com.fluxbank.commons_web.domain.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class RouteService {

    static final String ROUTE_KEY_PREFIX = "flux:route:";
    static final String IDENTITY_KEY_PREFIX = "flux:route:identity:";
    static final String SERVICE_INDEX_PREFIX = "flux:route:service:";
    static final String ENABLED_INDEX = "flux:route:enabled";

    private final RedisTemplate<String, Route> routeRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final RouteHasher routeHasher;

    public void syncServiceRoutes(String serviceName, List<Route> discovered) {
        String indexKey = serviceIndexKey(serviceName);
        Set<String> previousServiceHashes = members(indexKey);

        Map<String, List<Route>> byIdentity = groupByIdentity(discovered);
        Set<String> currentHashes = new HashSet<>();

        for (Map.Entry<String, List<Route>> entry : byIdentity.entrySet()) {
            String identityKey = identityKey(serviceName, entry.getKey());
            Set<String> previousIdentityHashes = members(identityKey);
            Set<String> currentIdentityHashes = new HashSet<>();

            for (Route route : entry.getValue()) {
                String hash = routeHasher.hash(route.method(), route.uri(), route.path());
                currentHashes.add(hash);
                currentIdentityHashes.add(hash);

                if (routeRedisTemplate.opsForValue().get(routeKey(hash)) == null) {
                    save(hash, route);
                }
            }

            for (String staleHash : previousIdentityHashes) {
                if (!currentIdentityHashes.contains(staleHash)) {
                    deleteByHash(staleHash, serviceName);
                }
            }

            replaceSet(identityKey, currentIdentityHashes);
        }

        for (String staleHash : previousServiceHashes) {
            if (!currentHashes.contains(staleHash)) {
                deleteByHash(staleHash, serviceName);
            }
        }

        replaceSet(indexKey, currentHashes);
    }

    public void enable(HttpMethod method, String uri, String path) {
        setEnabled(routeHasher.hash(method, uri, path), true);
    }

    public void disable(HttpMethod method, String uri, String path) {
        setEnabled(routeHasher.hash(method, uri, path), false);
    }

    public void enableByHash(String hash) {
        setEnabled(hash, true);
    }

    public void disableByHash(String hash) {
        setEnabled(hash, false);
    }

    public Route get(HttpMethod method, String uri, String path) {
        return routeRedisTemplate.opsForValue().get(routeKey(routeHasher.hash(method, uri, path)));
    }

    public Set<String> enabledHashes() {
        return members(ENABLED_INDEX);
    }

    private void setEnabled(String hash, boolean enabled) {
        String key = routeKey(hash);
        Route stored = routeRedisTemplate.opsForValue().get(key);
        if (stored == null) {
            log.warn("Cannot change enabled flag for missing route hash={}", hash);
            return;
        }

        routeRedisTemplate.opsForValue().set(key, stored.toBuilder().enabled(enabled).build());

        if (enabled) {
            stringRedisTemplate.opsForSet().add(ENABLED_INDEX, hash);
        } else {
            stringRedisTemplate.opsForSet().remove(ENABLED_INDEX, hash);
        }
    }

    private void save(String hash, Route route) {
        routeRedisTemplate.opsForValue().set(routeKey(hash), route);
        if (Boolean.TRUE.equals(route.enabled())) {
            stringRedisTemplate.opsForSet().add(ENABLED_INDEX, hash);
        } else {
            stringRedisTemplate.opsForSet().remove(ENABLED_INDEX, hash);
        }
    }

    private void deleteByHash(String hash, String serviceName) {
        if (hash == null || hash.isBlank()) {
            return;
        }

        Route stored = routeRedisTemplate.opsForValue().get(routeKey(hash));
        routeRedisTemplate.delete(routeKey(hash));
        stringRedisTemplate.opsForSet().remove(ENABLED_INDEX, hash);
        stringRedisTemplate.opsForSet().remove(serviceIndexKey(serviceName), hash);

        if (stored != null && stored.identity() != null) {
            String identityService = stored.service() != null ? stored.service() : serviceName;
            stringRedisTemplate.opsForSet().remove(identityKey(identityService, stored.identity()), hash);
            if (stored.service() != null && !stored.service().equals(serviceName)) {
                stringRedisTemplate.opsForSet().remove(serviceIndexKey(stored.service()), hash);
            }
        }
    }

    private Map<String, List<Route>> groupByIdentity(List<Route> discovered) {
        Map<String, List<Route>> byIdentity = new HashMap<>();
        for (Route route : discovered) {
            byIdentity.computeIfAbsent(route.identity(), ignored -> new ArrayList<>()).add(route);
        }
        return byIdentity;
    }

    private Set<String> members(String key) {
        Set<String> values = stringRedisTemplate.opsForSet().members(key);
        return values == null ? Set.of() : values;
    }

    private void replaceSet(String key, Set<String> values) {
        stringRedisTemplate.delete(key);
        if (!values.isEmpty()) {
            stringRedisTemplate.opsForSet().add(key, values.toArray(String[]::new));
        }
    }

    private static String routeKey(String hash) {
        return ROUTE_KEY_PREFIX + hash;
    }

    private static String identityKey(String service, String identity) {
        return IDENTITY_KEY_PREFIX + service + ":" + identity;
    }

    private static String serviceIndexKey(String service) {
        return SERVICE_INDEX_PREFIX + service;
    }

}
