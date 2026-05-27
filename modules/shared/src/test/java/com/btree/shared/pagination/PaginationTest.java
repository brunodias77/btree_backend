package com.btree.shared.pagination;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationTest {

    @Test
    void DEVE_ARREDONDAR_TOTAL_DE_PAGINAS_PARA_CIMA() {
        final var pagination = Pagination.of(List.of("a", "b"), 1, 2, 5);

        assertEquals(3, pagination.totalPages());
        assertTrue(pagination.hasNext());
        assertTrue(pagination.hasPrevious());
    }

    @Test
    void DEVE_CRIAR_PAGINACAO_VAZIA_COM_PAGINA_E_TAMANHO_DA_REQUISICAO() {
        final var pagination = Pagination.empty(PageRequest.of(3, 20));

        assertEquals(List.of(), pagination.items());
        assertEquals(3, pagination.currentPage());
        assertEquals(20, pagination.perPage());
        assertEquals(0, pagination.total());
        assertEquals(0, pagination.totalPages());
        assertFalse(pagination.hasNext());
    }

    @Test
    void DEVE_TRANSFORMAR_ITENS_E_PRESERVAR_METADADOS() {
        final var pagination = Pagination.of(List.of(1, 2, 3), 0, 3, 10);
        final var mapped = pagination.map(value -> "item-" + value);

        assertEquals(List.of("item-1", "item-2", "item-3"), mapped.items());
        assertEquals(0, mapped.currentPage());
        assertEquals(3, mapped.perPage());
        assertEquals(10, mapped.total());
    }
}
