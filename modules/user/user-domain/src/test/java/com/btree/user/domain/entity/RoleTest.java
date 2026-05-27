package com.btree.user.domain.entity;

import com.btree.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleTest {

    @Test
    void DEVE_CRIAR_ROLE_VALIDA() {
        final var role = Role.create("customer", "Cliente");

        assertNotNull(role.getId());
        assertEquals("customer", role.getName());
        assertEquals("Cliente", role.getDescription());
        assertNotNull(role.getCreatedAt());
    }

    @Test
    void DEVE_REJEITAR_ROLE_SEM_NOME() {
        final var exception = assertThrows(DomainException.class, () -> Role.create(" ", "Sem nome"));

        assertEquals("'name' não pode estar vazio", exception.getErrors().getFirst().message());
    }
}
