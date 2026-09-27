package com.fluxbank.commons_web.service;

import org.springframework.http.HttpMethod;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class RouteHasher {

    public String hash(HttpMethod method, String uri, String path) {
        String input = method.name() + ":" + uri + ":" + path;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

}
