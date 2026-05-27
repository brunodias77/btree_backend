package com.btree.shared.value_object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostalCodeTest {

    @Test
    void DEVE_FORMATAR_CEP_COM_HIFEN() {
        final var postalCode = PostalCode.of("01001000");

        assertEquals("01001-000", postalCode.formatted());
        assertEquals("01001000", postalCode.getDigits());
        assertEquals("01001-000", postalCode.toString());
    }

    @Test
    void DEVE_COMPARAR_USANDO_APENAS_DIGITOS() {
        assertEquals(PostalCode.of("01001000"), PostalCode.of("01001-000"));
    }

    @Test
    void DEVE_REJEITAR_CEP_INVALIDO() {
        assertThrows(IllegalArgumentException.class, () -> PostalCode.of("0100"));
    }
}
