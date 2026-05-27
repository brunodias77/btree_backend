package com.btree.shared.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationTest {

    @Test
    void DEVE_INICIAR_SEM_ERROS() {
        final var notification = Notification.create();

        assertFalse(notification.hasError());
        assertEquals(null, notification.firstError());
    }

    @Test
    void DEVE_ACUMULAR_ERROS_E_MESCLAR_HANDLERS() {
        final var first = Notification.create(new Error("first"));
        final var second = Notification.create(new Error("second"));

        first.append(second);

        assertTrue(first.hasError());
        assertEquals("first", first.firstError().message());
        assertEquals(2, first.getErrors().size());
    }

    @Test
    void DEVE_RETORNAR_LISTA_DE_ERROS_IMUTAVEL() {
        final var notification = Notification.create(new Error("error"));

        assertThrows(UnsupportedOperationException.class,
                () -> notification.getErrors().add(new Error("another")));
    }
}
