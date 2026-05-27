package com.btree.user.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTokenTest {

    @Test
    void DEVE_CRIAR_TOKEN_NAO_USADO_E_NAO_EXPIRADO() {
        final var userId = UUID.randomUUID();

        final var token = UserToken.create(userId, "EMAIL_VERIFICATION", "hash", Instant.now().plusSeconds(3600));

        assertNotNull(token.getId());
        assertEquals(userId, token.getUserId());
        assertEquals("EMAIL_VERIFICATION", token.getTokenType());
        assertFalse(token.isUsed());
        assertFalse(token.isExpired());
    }

    @Test
    void DEVE_MARCAR_TOKEN_COMO_USADO() {
        final var token = UserToken.create(UUID.randomUUID(), "EMAIL_VERIFICATION", "hash", Instant.now().plusSeconds(3600));

        token.markAsUsed();

        assertTrue(token.isUsed());
        assertNotNull(token.getUsedAt());
    }

    @Test
    void DEVE_IDENTIFICAR_TOKEN_EXPIRADO() {
        final var token = UserToken.with(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "EMAIL_VERIFICATION",
                "hash",
                Instant.now().minusSeconds(1),
                null,
                Instant.now().minusSeconds(3600));

        assertTrue(token.isExpired());
    }

    @Test
    void DEVE_REJEITAR_CAMPOS_OBRIGATORIOS_NULOS() {
        assertThrows(NullPointerException.class,
                () -> UserToken.create(null, "EMAIL_VERIFICATION", "hash", Instant.now()));
        assertThrows(NullPointerException.class,
                () -> UserToken.create(UUID.randomUUID(), null, "hash", Instant.now()));
        assertThrows(NullPointerException.class,
                () -> UserToken.create(UUID.randomUUID(), "EMAIL_VERIFICATION", null, Instant.now()));
        assertThrows(NullPointerException.class,
                () -> UserToken.create(UUID.randomUUID(), "EMAIL_VERIFICATION", "hash", null));
    }
}
