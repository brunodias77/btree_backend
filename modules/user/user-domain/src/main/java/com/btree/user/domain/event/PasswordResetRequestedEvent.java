package com.btree.user.domain.event;

import com.btree.shared.event.DomainEvent;

import java.time.Instant;

public class PasswordResetRequestedEvent extends DomainEvent {

    private static final String MODULE = "users";
    private static final String AGGREGATE_TYPE = "User";
    private static final String EVENT_TYPE = "PasswordResetRequested";

    private final String email;
    private final String rawToken;
    private final Instant expiresAt;

    public PasswordResetRequestedEvent(
            final String aggregateId,
            final String email,
            final String rawToken,
            final Instant expiresAt
    ) {
        super(MODULE, AGGREGATE_TYPE, aggregateId);
        this.email = email;
        this.rawToken = rawToken;
        this.expiresAt = expiresAt;
    }

    @Override
    public String getEventType() { return EVENT_TYPE; }

    public String getEmail() { return email; }
    public String getRawToken() { return rawToken; }
    public Instant getExpiresAt() { return expiresAt; }
}
