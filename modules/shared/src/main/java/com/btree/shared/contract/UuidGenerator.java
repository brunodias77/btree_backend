package com.btree.shared.contract;

/**
 * Porta para geração de UUIDs.
 * Implementação padrão: UuidV7Generator em infrastructure, usando
 * a função PostgreSQL {@code uuid_generate_v7()} ou geração local time-ordered.
 */
public interface UuidGenerator {

    /**
     * Gera um novo UUID único.
     *
     * @return UUID como string no formato padrão (ex: {@code 550e8400-e29b-41d4-a716-446655440000})
     */
    String generate();
}
