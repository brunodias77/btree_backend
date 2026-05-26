package com.btree.shared.pagination;
/**
 * Parâmetros de paginação vindos do caller (use case ou controller).
 *
 * @param page  número da página, zero-based
 * @param size  quantidade de itens por página
 */
public record PageRequest(int page, int size) {

    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("'page' deve ser maior ou igual a 0");
        if (size < 1) throw new IllegalArgumentException("'size' deve ser maior ou igual a 1");
    }

    public static PageRequest of(final int page, final int size) {
        return new PageRequest(page, size);
    }

    public int offset() {
        return page * size;
    }
}

