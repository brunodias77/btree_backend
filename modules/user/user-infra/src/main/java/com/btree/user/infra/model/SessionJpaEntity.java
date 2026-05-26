package com.btree.user.infra.model;

import com.btree.user.domain.aggregate_root.Session;
import com.btree.user.domain.value_object.DeviceInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessions", schema = "users")
public class SessionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "refresh_token_hash", nullable = false, length = 512)
    private String refreshTokenHash;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_reason", length = 100)
    private String revokedReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    public SessionJpaEntity() {}

    private SessionJpaEntity(
            final UUID id,
            final UUID userId,
            final String refreshTokenHash,
            final String ipAddress,
            final boolean isCurrent,
            final Instant expiresAt,
            final Instant revokedAt,
            final String revokedReason,
            final Instant createdAt,
            final Instant lastActivityAt
    ) {
        this.id = id;
        this.userId = userId;
        this.refreshTokenHash = refreshTokenHash;
        this.ipAddress = ipAddress;
        this.isCurrent = isCurrent;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.revokedReason = revokedReason;
        this.createdAt = createdAt;
        this.lastActivityAt = lastActivityAt;
    }

    public static SessionJpaEntity from(final Session session) {
        final var deviceInfo = session.getDeviceInfo();
        return new SessionJpaEntity(
                session.getId(),
                session.getUserId(),
                session.getRefreshTokenHash(),
                deviceInfo != null ? deviceInfo.getIpAddress() : null,
                session.isCurrent(),
                session.getExpiresAt(),
                session.getRevokedAt(),
                session.getRevokedReason(),
                session.getCreatedAt(),
                session.getLastActivityAt()
        );
    }

    public Session toAggregate() {
        return Session.with(
                this.id,
                this.userId,
                this.refreshTokenHash,
                DeviceInfo.of(this.ipAddress, null),
                this.isCurrent,
                this.expiresAt,
                this.revokedAt,
                this.revokedReason,
                this.createdAt,
                this.lastActivityAt,
                this.version
        );
    }

    public void updateFrom(final Session session) {
        this.isCurrent = session.isCurrent();
        this.revokedAt = session.getRevokedAt();
        this.revokedReason = session.getRevokedReason();
        this.lastActivityAt = session.getLastActivityAt();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getRefreshTokenHash() { return refreshTokenHash; }
    public String getIpAddress() { return ipAddress; }
    public boolean isCurrent() { return isCurrent; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
    public String getRevokedReason() { return revokedReason; }
    public void setRevokedReason(String revokedReason) { this.revokedReason = revokedReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    public int getVersion() { return version; }
}
