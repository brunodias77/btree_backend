package com.btree.user.domain.aggregate_root;

import com.btree.shared.exception.DomainException;
import com.btree.user.domain.value_object.DeviceInfo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionTest {

    @Test
    void DEVE_CRIAR_SESSAO_ATIVA() {
        final var userId = UUID.randomUUID();
        final var deviceInfo = DeviceInfo.of("127.0.0.1", "JUnit");

        final var session = Session.create(userId, "refresh-hash", deviceInfo, Instant.now().plusSeconds(3600));

        assertNotNull(session.getId());
        assertEquals(userId, session.getUserId());
        assertEquals("refresh-hash", session.getRefreshTokenHash());
        assertEquals(deviceInfo, session.getDeviceInfo());
        assertTrue(session.isCurrent());
        assertTrue(session.isActive());
        assertFalse(session.isExpired());
        assertEquals(1, session.getVersion());
    }

    @Test
    void DEVE_REJEITAR_SESSAO_INVALIDA() {
        final var exception = assertThrows(DomainException.class,
                () -> Session.create(null, " ", DeviceInfo.of("127.0.0.1", "JUnit"), null));

        assertEquals(3, exception.getErrors().size());
    }

    @Test
    void DEVE_REVOGAR_SESSAO_E_INCREMENTAR_VERSAO() {
        final var session = Session.create(
                UUID.randomUUID(),
                "refresh-hash",
                DeviceInfo.of("127.0.0.1", "JUnit"),
                Instant.now().plusSeconds(3600));
        final var version = session.getVersion();

        session.revoke("logout");

        assertTrue(session.isRevoked());
        assertFalse(session.isCurrent());
        assertFalse(session.isActive());
        assertEquals("logout", session.getRevokedReason());
        assertNotNull(session.getRevokedAt());
        assertEquals(session.getRevokedAt(), session.getLastActivityAt());
        assertEquals(version + 1, session.getVersion());
    }

    @Test
    void DEVE_IDENTIFICAR_SESSAO_EXPIRADA() {
        final var session = Session.with(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "refresh-hash",
                DeviceInfo.of("127.0.0.1", "JUnit"),
                true,
                Instant.now().minusSeconds(1),
                null,
                null,
                Instant.now().minusSeconds(3600),
                Instant.now().minusSeconds(3600),
                4);

        assertTrue(session.isExpired());
        assertFalse(session.isActive());
    }
}
