package com.btree.shared.value_object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpfTest {

    @Test
    void DEVE_ACEITAR_CPF_FORMATADO_VALIDO() {
        final var cpf = Cpf.of("529.982.247-25");

        assertEquals("529.982.247-25", cpf.getValue());
        assertEquals("52998224725", cpf.getDigits());
    }

    @Test
    void DEVE_FORMATAR_CPF_A_PARTIR_DE_DIGITOS_VALIDOS() {
        final var cpf = Cpf.ofUnformatted("52998224725");

        assertEquals("529.982.247-25", cpf.getValue());
    }

    @Test
    void DEVE_REJEITAR_CPF_INVALIDO() {
        assertThrows(IllegalArgumentException.class, () -> Cpf.of("111.111.111-11"));
    }
}
