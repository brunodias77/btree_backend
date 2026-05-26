package com.btree.user.domain.event;

import com.btree.shared.event.DomainEvent;

import java.time.Instant;

public class UserAccountLockedEvent extends DomainEvent {

    private static final String MODULE = "users";
    private static final String AGGREGATE_TYPE = "User";
    private static final String EVENT_TYPE = "UserAccountLocked";

    private final Instant lockExpiresAt;

    public UserAccountLockedEvent(final String aggregateId, final Instant lockExpiresAt) {
        super(MODULE, AGGREGATE_TYPE, aggregateId);
        this.lockExpiresAt = lockExpiresAt;
    }

    @Override
    public String getEventType() { return EVENT_TYPE; }

    public Instant getLockExpiresAt() { return lockExpiresAt; }
}
