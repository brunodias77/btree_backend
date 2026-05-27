package com.btree.user.infra.persistence;

import com.btree.shared.exception.NotFoundException;
import com.btree.user.domain.aggregate_root.Session;
import com.btree.user.domain.value_object.DeviceInfo;
import com.btree.user.infra.model.SessionJpaEntity;
import com.btree.user.infra.repository.SessionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionPostgresGatewayTest {

    private SessionJpaRepository repository;
    private SessionPostgresGateway gateway;

    @BeforeEach
    void SETUP() {
        repository = mock(SessionJpaRepository.class);
        gateway = new SessionPostgresGateway(repository);
    }

    @Test
    void DEVE_CRIAR_SESSAO() {
        final var session = sessaoValida();
        when(repository.save(any(SessionJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var result = gateway.create(session);

        assertEquals(session.getId(), result.getId());
        assertEquals(session.getRefreshTokenHash(), result.getRefreshTokenHash());
    }

    @Test
    void DEVE_ATUALIZAR_SESSAO_EXISTENTE() {
        final var session = sessaoValida();
        final var entity = SessionJpaEntity.from(session);
        session.revoke("logout");
        when(repository.findById(session.getId())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        final var result = gateway.update(session);

        assertFalse(result.isCurrent());
        assertEquals("logout", result.getRevokedReason());
        verify(repository).save(entity);
    }

    @Test
    void DEVE_LANCAR_NOT_FOUND_AO_ATUALIZAR_SESSAO_INEXISTENTE() {
        final var session = sessaoValida();
        when(repository.findById(session.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gateway.update(session));

        verify(repository, never()).save(any(SessionJpaEntity.class));
    }

    @Test
    void DEVE_BUSCAR_E_REVOGAR_SESSAO_POR_REFRESH_TOKEN() {
        final var entity = SessionJpaEntity.from(sessaoValida());
        final var now = Instant.now();
        when(repository.findByRefreshTokenHash("refresh-hash")).thenReturn(Optional.of(entity));
        when(repository.revokeActiveByRefreshTokenHash("refresh-hash", now)).thenReturn(Optional.of(entity));

        assertTrue(gateway.findByRefreshTokenHash("refresh-hash").isPresent());
        assertTrue(gateway.revokeActiveByRefreshTokenHash("refresh-hash", now).isPresent());
    }

    @Test
    void DEVE_REVOGAR_TODAS_AS_SESSOES_ATIVAS_DO_USUARIO() {
        final var userId = UUID.randomUUID();
        when(repository.revokeAllActiveByUserId(eq(userId), any(Instant.class))).thenReturn(4);

        assertEquals(4, gateway.revokeAllByUserId(userId));
    }

    private static Session sessaoValida() {
        return Session.create(
                UUID.randomUUID(),
                "refresh-hash",
                DeviceInfo.of("127.0.0.1", "JUnit"),
                Instant.now().plusSeconds(3600));
    }
}
