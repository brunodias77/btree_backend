package com.btree.user.domain.entity;

import com.btree.shared.validation.Notification;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressTest {

    @Test
    void DEVE_CRIAR_PRIMEIRO_ENDERECO_COMO_PADRAO() {
        final var notification = Notification.create();
        final var userId = UUID.randomUUID();

        final var address = Address.create(
                userId,
                "Casa",
                "Cliente Teste",
                "Rua das Flores",
                "123",
                "Apto 1",
                "Centro",
                "Sao Paulo",
                "SP",
                "01001-000",
                null,
                true,
                true,
                notification);

        assertFalse(notification.hasError());
        assertEquals(userId, address.getUserId());
        assertEquals("BR", address.getCountry());
        assertTrue(address.isDefault());
        assertTrue(address.isBillingAddress());
        assertNotNull(address.getCreatedAt());
        assertNotNull(address.getUpdatedAt());
    }

    @Test
    void DEVE_ACUMULAR_ERROS_DE_ENDERECO_INVALIDO() {
        final var notification = Notification.create();

        Address.create(null, "Casa", "Cliente", "", "123", null, null, "", "sp", "abc", "", false, false, notification);

        assertTrue(notification.hasError());
        assertEquals(6, notification.getErrors().size());
    }

    @Test
    void DEVE_ATUALIZAR_DADOS_E_REMOVER_LOGICAMENTE() {
        final var address = enderecoValido();

        address.updateData(
                "Trabalho",
                "Outro Cliente",
                "Avenida Paulista",
                "1000",
                null,
                "Bela Vista",
                "Sao Paulo",
                "SP",
                "01310-100",
                null,
                false);
        address.unsetDefault();
        address.softDelete();

        assertEquals("Trabalho", address.getLabel());
        assertEquals("BR", address.getCountry());
        assertFalse(address.isBillingAddress());
        assertFalse(address.isDefault());
        assertTrue(address.isDeleted());
        assertNotNull(address.getDeletedAt());
    }

    private static Address enderecoValido() {
        return Address.create(
                UUID.randomUUID(),
                "Casa",
                "Cliente Teste",
                "Rua das Flores",
                "123",
                null,
                "Centro",
                "Sao Paulo",
                "SP",
                "01001-000",
                "BR",
                false,
                true,
                Notification.create());
    }
}
