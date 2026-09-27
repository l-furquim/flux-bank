package com.fluxbank.commons_core.domain.id;

import com.fluxbank.commons_core.utils.UuidV7IdGenerator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainIdTest {

    @Test
    void parsesPrefixedUuid() {
        UUID value = UUID.fromString("0199aabb-ccdd-7eef-8000-111122223333");
        RequestId id = new RequestId(value);

        assertEquals("req_0199aabbccdd7eef8000111122223333", id._asString());
        assertEquals(id, RequestId.parse(id._asString()));
    }

    @Test
    void rejectsWrongPrefixAndUnsafeHeader() {
        assertTrue(RequestId.tryParse("acc_0199aabbccdd7eef8000111122223333").isEmpty());
        assertTrue(RequestId.tryParse("req_0199aabbccdd7eef8000111122223333\n").isEmpty());
        assertTrue(AccountId.tryParse("acc_0199aabbccdd7eef8000111122223333").isPresent());
    }

    @Test
    void generatesUuidVersion7() {
        assertEquals(7, new UuidV7IdGenerator().generate().version());
    }
}
