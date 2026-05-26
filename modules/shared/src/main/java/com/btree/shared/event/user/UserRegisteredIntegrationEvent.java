package com.btree.shared.event.user;

import com.btree.shared.event.IntegrationEvent;

import java.util.UUID;

public class UserRegisteredIntegrationEvent extends IntegrationEvent {

    private static final String SOURCE = "users";
    private static final String EVENT_TYPE = "UserRegistered";

    private final UUID userId;
    private final String email;

    public UserRegisteredIntegrationEvent(final UUID userId, final String email) {
        super(SOURCE, "User", userId.toString());
        this.userId = userId;
        this.email = email;
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
