package com.btree.user.infra.repository;

import com.btree.user.infra.model.UserTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserTokenJpaRepository extends JpaRepository<UserTokenJpaEntity, UUID> {

    Optional<UserTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query(value = """
        UPDATE users.user_tokens
        SET used_at = :usedAt
        WHERE id = :tokenId
          AND used_at IS NULL
          AND expires_at > :usedAt
        """, nativeQuery = true)
    int markAsUsedIfActive(
            @Param("tokenId") UUID tokenId,
            @Param("usedAt") Instant usedAt
    );

    /**
     * Remove fisicamente tokens expirados em lote.
     * A cláusula {@code WHERE id IN (SELECT id ... LIMIT :batchSize)} garante
     * que o DELETE respeite o limite de lote sem travar a tabela inteira.
     */
    @Modifying
    @Query(value = """
        DELETE FROM users.user_tokens
        WHERE id IN (
            SELECT id FROM users.user_tokens
            WHERE expires_at < :now
            LIMIT :batchSize
        )
        """, nativeQuery = true)
    int deleteExpiredBatch(@Param("now") Instant now, @Param("batchSize") int batchSize);

    @Modifying
    @Query(value = """
        UPDATE users.user_tokens
        SET used_at = :now
        WHERE user_id = :userId
          AND token_type = :tokenType
          AND used_at IS NULL
          AND expires_at > :now
        """, nativeQuery = true)
    int markActiveAsUsedByUserIdAndType(
            @Param("userId") UUID userId,
            @Param("tokenType") String tokenType,
            @Param("now") Instant now
    );
}
