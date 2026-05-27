package com.btree.user.domain.entity;

import com.btree.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserNotificationTest {

    @Test
    void DEVE_CRIAR_NOTIFICACAO_NAO_LIDA() {
        final var userId = UUID.randomUUID();

        final var notification = UserNotification.create(userId, "Pedido enviado", "Seu pedido saiu", "ORDER");

        assertEquals(userId, notification.getUserId());
        assertEquals("Pedido enviado", notification.getTitle());
        assertFalse(notification.isRead());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void DEVE_MARCAR_NOTIFICACAO_COMO_LIDA_APENAS_UMA_VEZ() {
        final var notification = UserNotification.create(UUID.randomUUID(), "Titulo", "Mensagem", "INFO");

        notification.markAsRead();
        final var readAt = notification.getReadAt();
        notification.markAsRead();

        assertTrue(notification.isRead());
        assertEquals(readAt, notification.getReadAt());
    }

    @Test
    void DEVE_REJEITAR_NOTIFICACAO_INVALIDA() {
        final var exception = assertThrows(DomainException.class,
                () -> UserNotification.create(UUID.randomUUID(), " ", "", null));

        assertEquals(3, exception.getErrors().size());
    }
}
