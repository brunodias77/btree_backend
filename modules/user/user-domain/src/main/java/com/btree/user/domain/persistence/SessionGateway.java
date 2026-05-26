package com.btree.user.domain.persistence;


import com.btree.user.domain.aggregate_root.Session;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionGateway {
    Session create(Session session);
    Session update(Session session);
    Optional<Session> findByRefreshTokenHash(String refreshTokenHash);
    Optional<Session> revokeActiveByRefreshTokenHash(String refreshTokenHash, Instant now);
    int revokeAllByUserId(UUID userId);
}