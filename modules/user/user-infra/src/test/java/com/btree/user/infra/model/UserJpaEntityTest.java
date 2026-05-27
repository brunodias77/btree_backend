package com.btree.user.infra.model;

import com.btree.shared.validation.Notification;
import com.btree.user.domain.aggregate_root.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserJpaEntityTest {

    @Test
    void DEVE_CONVERTER_AGREGADO_PARA_ENTIDADE_JPA_COM_RELACIONAMENTOS() {
        final var user = User.create("cliente_teste", "cliente@example.com", "hash-senha", Notification.create());

        final var entity = UserJpaEntity.from(user);

        assertEquals(user.getId(), entity.getId());
        assertEquals("cliente_teste", entity.getUsername());
        assertEquals("cliente@example.com", entity.getEmail());
        assertFalse(entity.isEmailVerified());
        assertEquals("hash-senha", entity.getPasswordHash());
        assertNotNull(entity.getProfile());
        assertNotNull(entity.getNotificationPreference());
        assertSame(entity, entity.getProfile().getUser());
        assertSame(entity, entity.getNotificationPreference().getUser());
    }

    @Test
    void DEVE_CONVERTER_ENTIDADE_JPA_PARA_AGREGADO() {
        final var id = UUID.randomUUID();
        final var createdAt = Instant.parse("2026-01-01T00:00:00Z");
        final var updatedAt = Instant.parse("2026-01-02T00:00:00Z");
        final var entity = new UserJpaEntity();
        entity.setId(id);
        entity.setUsername("cliente_teste");
        entity.setEmail("cliente@example.com");
        entity.setEmailVerified(true);
        entity.setPasswordHash("hash-senha");
        entity.setPhoneNumber("+5511999999999");
        entity.setPhoneNumberVerified(true);
        entity.setTwoFactorEnabled(true);
        entity.setAccountLocked(false);
        entity.setAccessFailedCount(2);
        entity.setEnabled(true);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);
        entity.setVersion(7);

        final var aggregate = entity.toAggregate();

        assertEquals(id, aggregate.getId());
        assertEquals("cliente_teste", aggregate.getUsername());
        assertEquals("cliente@example.com", aggregate.getEmail());
        assertTrue(aggregate.isEmailVerified());
        assertEquals("+5511999999999", aggregate.getPhoneNumber());
        assertTrue(aggregate.isPhoneNumberVerified());
        assertTrue(aggregate.isTwoFactorEnabled());
        assertEquals(2, aggregate.getAccessFailedCount());
        assertEquals(7, aggregate.getVersion());
    }

    @Test
    void DEVE_ATUALIZAR_CAMPOS_MUTAVEIS_A_PARTIR_DO_AGREGADO() {
        final var original = User.create("cliente_teste", "cliente@example.com", "hash-senha", Notification.create());
        final var entity = UserJpaEntity.from(original);
        final var atualizado = User.with(
                original.getId(),
                "cliente_novo",
                "novo@example.com",
                true,
                "novo-hash",
                "+5511888888888",
                true,
                true,
                "segredo",
                true,
                Instant.parse("2026-01-03T00:00:00Z"),
                3,
                false,
                original.getCreatedAt(),
                Instant.parse("2026-01-04T00:00:00Z"),
                original.getVersion());

        entity.updateFrom(atualizado);

        assertEquals("cliente_novo", entity.getUsername());
        assertEquals("novo@example.com", entity.getEmail());
        assertTrue(entity.isEmailVerified());
        assertEquals("novo-hash", entity.getPasswordHash());
        assertEquals("+5511888888888", entity.getPhoneNumber());
        assertTrue(entity.isAccountLocked());
        assertFalse(entity.isEnabled());
    }
}
