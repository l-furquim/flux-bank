package com.fluxbank.commons_core.domain.id;

import java.util.Optional;
import java.util.UUID;

public final class DomainIds {

    public static final int MAX_HEADER_LENGTH = 128;

    private DomainIds() {
    }

    public static UUID parse(String prefix, String raw) {
        return tryParse(prefix, raw)
                .orElseThrow(() -> new IllegalArgumentException("Invalid " + prefix + " id"));
    }

    public static Optional<UUID> tryParse(String prefix, String raw) {
        if (!isSafeHeader(raw)) {
            return Optional.empty();
        }
        String token = prefix + "_";
        if (!raw.startsWith(token) || raw.length() != token.length() + 32) {
            return Optional.empty();
        }
        String hex = raw.substring(token.length());
        if (!isHex(hex)) {
            return Optional.empty();
        }
        return Optional.of(fromHex(hex));
    }

    public static boolean isSafeHeader(String raw) {
        if (raw == null || raw.isBlank() || raw.length() > MAX_HEADER_LENGTH) {
            return false;
        }
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '\r' || c == '\n' || c == '\0') {
                return false;
            }
        }
        return true;
    }

    private static boolean isHex(String hex) {
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            boolean digit = c >= '0' && c <= '9';
            boolean lower = c >= 'a' && c <= 'f';
            boolean upper = c >= 'A' && c <= 'F';
            if (!digit && !lower && !upper) {
                return false;
            }
        }
        return true;
    }

    private static UUID fromHex(String hex) {
        String dashed = hex.substring(0, 8)
                + "-"
                + hex.substring(8, 12)
                + "-"
                + hex.substring(12, 16)
                + "-"
                + hex.substring(16, 20)
                + "-"
                + hex.substring(20);
        return UUID.fromString(dashed);
    }
}
