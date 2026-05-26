package com.btree.user.domain.event;

import com.btree.shared.event.DomainEvent;

public class UserEmailVerifiedEvent extends DomainEvent {

    private static final String MODULE = "users";
    private static final String AGGREGATE_TYPE = "User";
    private static final String EVENT_TYPE = "UserEmailVerified";

    public UserEmailVerifiedEvent(final String aggregateId) {
        super(MODULE, AGGREGATE_TYPE, aggregateId);
    }

    @Override
    public String getEventType() { return EVENT_TYPE; }
}
