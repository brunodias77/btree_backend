package com.btree.user.domain.persistence;


import com.btree.user.domain.entity.UserToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserTokenGateway {
    UserToken create(UserToken userToken);
    UserToken update(UserToken userToken);
    Optional<UserToken> findByTokenHash(String tokenHash);
    Optional<UserToken> findById(UUID id);
    boolean markAsUsedIfActive(UUID tokenId, Instant usedAt);

    /**
     * Marca como usados todos os tokens ativos (não expirados e não usados)
     * de um determinado tipo para o usuário informado.
     */
    void invalidateActiveByUserIdAndType(UUID userId, String tokenType);

    /**
     * Remove fisicamente tokens expirados em lote.
     *
     * @param batchSize limite máximo de registros a deletar nesta execução
     * @return número de registros efetivamente deletados
     */
    int deleteExpired(int batchSize);
}
