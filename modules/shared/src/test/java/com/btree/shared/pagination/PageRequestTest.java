package com.btree.shared.pagination;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageRequestTest {

    @Test
    void DEVE_CALCULAR_OFFSET_COM_PAGINA_ZERO_BASED() {
        final var request = PageRequest.of(2, 25);

        assertEquals(50, request.offset());
    }

    @Test
    void DEVE_REJEITAR_PAGINA_NEGATIVA() {
        assertThrows(IllegalArgumentException.class, () -> PageRequest.of(-1, 10));
    }

    @Test
    void DEVE_REJEITAR_TAMANHO_NAO_POSITIVO() {
        assertThrows(IllegalArgumentException.class, () -> PageRequest.of(0, 0));
    }
}
