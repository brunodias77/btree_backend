package com.btree.shared.gateway;

import java.util.UUID;

/**
 * Porta de saída para a tabela de auditoria particionada {@code shared.audit_logs}.
 *
 * <p>Registra alterações de estado em entidades de qualquer módulo, incluindo
 * os valores anteriores ({@code old_values}) e novos ({@code new_values}) em JSON,
 * o usuário que executou a ação e o contexto HTTP de origem.
 *
 * <p>A implementação deve inserir na partição correta conforme {@code createdAt}.
 */
public interface AuditLogGateway {

    /**
     * Registra uma entrada de auditoria.
     *
     * @param module      módulo de origem (ex: {@code "catalog"}, {@code "orders"})
     * @param entityType  tipo da entidade auditada (ex: {@code "Product"})
     * @param entityId    UUID da entidade auditada
     * @param action      ação realizada (ex: {@code "CREATE"}, {@code "UPDATE"}, {@code "DELETE"})
     * @param oldValues   estado anterior serializado em JSON; {@code null} para criações
     * @param newValues   novo estado serializado em JSON; {@code null} para deleções
     * @param userId      UUID do usuário que realizou a ação; {@code null} se for ação de sistema
     * @param ipAddress   endereço IP do cliente; {@code null} se indisponível
     * @param userAgent   user-agent HTTP; {@code null} se indisponível
     */
    void record(
            String module,
            String entityType,
            UUID entityId,
            String action,
            String oldValues,
            String newValues,
            UUID userId,
            String ipAddress,
            String userAgent
    );
}
