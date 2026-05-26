package com.btree.user.domain.aggregate_root;

import com.btree.shared.abstraction.AggregateRoot;
import com.btree.shared.exception.DomainException;
import com.btree.shared.util.UuidV7;
import com.btree.shared.validation.Notification;
import com.btree.shared.validation.ValidationHandler;
import com.btree.user.domain.validator.SessionValidator;
import com.btree.user.domain.value_object.DeviceInfo;

import java.time.Instant;
import java.util.UUID;

public class Session extends AggregateRoot {

    private final UUID userId;
    private final String refreshTokenHash;
    private final DeviceInfo deviceInfo;
    private boolean isCurrent;
    private final Instant expiresAt;
    private Instant revokedAt;
    private String revokedReason;
    private final Instant createdAt;
    private Instant lastActivityAt;

    private Session(
            final UUID id,
            final UUID userId,
            final String refreshTokenHash,
            final DeviceInfo deviceInfo,
            final boolean isCurrent,
            final Instant expiresAt,
            final Instant revokedAt,
            final String revokedReason,
            final Instant createdAt,
            final Instant lastActivityAt,
            final int version
    ) {
        super(id, version);
        this.userId = userId;
        this.refreshTokenHash = refreshTokenHash;
        this.deviceInfo = deviceInfo;
        this.isCurrent = isCurrent;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.revokedReason = revokedReason;
        this.createdAt = createdAt;
        this.lastActivityAt = lastActivityAt;
    }

    public static Session create(
            final UUID userId,
            final String refreshTokenHash,
            final DeviceInfo deviceInfo,
            final Instant expiresAt
    ) {
        final var now = Instant.now();
        final var notification = Notification.create();
        final var session = new Session(
                UuidV7.generate(), userId, refreshTokenHash,
                deviceInfo, true, expiresAt,
                null, null, now, now, 1
        );

        session.validate(notification);
        if (notification.hasError()) {
            throw new DomainException(notification.getErrors());
        }

        return session;
    }

    public static Session with(
            final UUID id,
            final UUID userId,
            final String refreshTokenHash,
            final DeviceInfo deviceInfo,
            final boolean isCurrent,
            final Instant expiresAt,
            final Instant revokedAt,
            final String revokedReason,
            final Instant createdAt,
            final Instant lastActivityAt,
            final int version
    ) {
        return new Session(
                id, userId, refreshTokenHash, deviceInfo, isCurrent,
                expiresAt, revokedAt, revokedReason, createdAt, lastActivityAt, version
        );
    }

    public void revoke(final String reason) {
        apply(() -> {
            this.revokedAt = Instant.now();
            this.revokedReason = reason;
            this.isCurrent = false;
            this.lastActivityAt = this.revokedAt;
        });
    }

    public void refreshActivity() {
        apply(() -> this.lastActivityAt = Instant.now());
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isRevoked() {
        return this.revokedAt != null;
    }

    public boolean isActive() {
        return !isRevoked() && !isExpired();
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new SessionValidator(this, handler).validate();
    }

    public UUID getUserId()           { return userId; }
    public String getRefreshTokenHash() { return refreshTokenHash; }
    public DeviceInfo getDeviceInfo() { return deviceInfo; }
    public boolean isCurrent()        { return isCurrent; }
    public Instant getExpiresAt()     { return expiresAt; }
    public Instant getRevokedAt()     { return revokedAt; }
    public String getRevokedReason()  { return revokedReason; }
    public Instant getCreatedAt()     { return createdAt; }
    public Instant getLastActivityAt(){ return lastActivityAt; }
}
