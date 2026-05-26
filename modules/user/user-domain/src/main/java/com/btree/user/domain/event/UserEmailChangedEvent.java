package com.btree.user.domain.event;

import com.btree.shared.event.DomainEvent;

public class UserEmailChangedEvent extends DomainEvent {

    private static final String MODULE = "users";
    private static final String AGGREGATE_TYPE = "User";
    private static final String EVENT_TYPE = "UserEmailChanged";

    private final String newEmail;

    public UserEmailChangedEvent(final String aggregateId, final String newEmail) {
        super(MODULE, AGGREGATE_TYPE, aggregateId);
        this.newEmail = newEmail;
    }

    @Override
    public String getEventType() { return EVENT_TYPE; }

    public String getNewEmail() { return newEmail; }
}
