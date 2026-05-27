package com.btree.shared.value_object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SlugTest {

    @Test
    void DEVE_ACEITAR_SLUG_JA_FORMATADO() {
        final var slug = Slug.of("camiseta-azul-100");

        assertEquals("camiseta-azul-100", slug.getValue());
    }

    @Test
    void DEVE_NORMALIZAR_TEXTO_PARA_SLUG() {
        final var slug = Slug.slugify("Camiseta Azul Marinho 100% Algodao");

        assertEquals("camiseta-azul-marinho-100-algodao", slug.getValue());
    }

    @Test
    void DEVE_REJEITAR_SLUG_INVALIDO() {
        assertThrows(IllegalArgumentException.class, () -> Slug.of("Invalid Slug"));
    }
}
