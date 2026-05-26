package com.btree.shared.value_object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void DEVE_NORMALIZAR_EMAIL() {
        final var email = Email.of("  USER.Name+Tag@Example.COM  ");

        assertEquals("user.name+tag@example.com", email.getValue());
        assertEquals("user.name+tag@example.com", email.toString());
    }

    @Test
    void DEVE_REJEITAR_EMAIL_INVALIDO() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("not-an-email"));
    }

    @Test
    void DEVE_REJEITAR_EMAIL_EM_BRANCO() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("   "));
    }
}
