package com.btree.shared.value_object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneNumberTest {

    @Test
    void DEVE_MANTER_VALOR_E_EXPOR_APENAS_DIGITOS() {
        final var phone = PhoneNumber.of("+55 (11) 99999-9999");

        assertEquals("+55 (11) 99999-9999", phone.getValue());
        assertEquals("5511999999999", phone.getDigits());
    }

    @Test
    void DEVE_COMPARAR_USANDO_APENAS_DIGITOS() {
        final var formatted = PhoneNumber.of("(11) 99999-9999");
        final var digits = PhoneNumber.of("11999999999");

        assertEquals(formatted, digits);
    }

    @Test
    void DEVE_REJEITAR_TELEFONE_INVALIDO() {
        assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of("(---)"));
    }
}
