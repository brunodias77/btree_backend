package com.btree.user.infra.repository;

import com.btree.user.infra.model.SessionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionJpaRepository extends JpaRepository<SessionJpaEntity, UUID> {

    Optional<SessionJpaEntity> findByRefreshTokenHash(String refreshTokenHash);

    @Query(value = """
        UPDATE users.sessions
        SET revoked_at = :now,
            is_current = false,
            version    = version + 1
        WHERE refresh_token_hash = :refreshTokenHash
          AND revoked_at IS NULL
          AND expires_at > :now
        RETURNING *
        """, nativeQuery = true)
    Optional<SessionJpaEntity> revokeActiveByRefreshTokenHash(
            @Param("refreshTokenHash") String refreshTokenHash,
            @Param("now") Instant now
    );

    @Modifying
    @Query("""
        UPDATE SessionJpaEntity s
        SET s.revokedAt = :now,
            s.isCurrent = false
        WHERE s.userId = :userId
          AND s.revokedAt IS NULL
          AND s.expiresAt > :now
        """)
    int revokeAllActiveByUserId(@Param("userId") UUID userId, @Param("now") Instant now);
}