package com.btree.shared.pagination;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchQueryTest {

    @Test
    void DEVE_APARAR_CAMPOS_E_DEFINIR_PADROES_QUANDO_NULOS() {
        final var query = SearchQuery.of(0, 10, "  shoes  ", " name ", " DESC ");

        assertEquals("shoes", query.terms());
        assertEquals("name", query.sort());
        assertEquals("DESC", query.direction());
        assertTrue(query.hasTerms());
        assertFalse(query.isAscending());
    }

    @Test
    void DEVE_USAR_ORDENACAO_E_DIRECAO_PADRAO() {
        final var query = SearchQuery.of(1, 20, null);

        assertEquals("", query.terms());
        assertEquals("createdAt", query.sort());
        assertEquals("asc", query.direction());
        assertFalse(query.hasTerms());
        assertTrue(query.isAscending());
        assertEquals(PageRequest.of(1, 20), query.toPageRequest());
    }

    @Test
    void DEVE_REJEITAR_PAGINACAO_INVALIDA() {
        assertThrows(IllegalArgumentException.class, () -> SearchQuery.of(-1, 10, ""));
        assertThrows(IllegalArgumentException.class, () -> SearchQuery.of(0, 0, ""));
    }
}
