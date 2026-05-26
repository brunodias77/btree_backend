package com.btree.user.domain.entity;

import com.btree.shared.abstraction.Entity;
import com.btree.shared.exception.DomainException;
import com.btree.shared.util.UuidV7;
import com.btree.shared.validation.Notification;
import com.btree.shared.validation.ValidationHandler;
import com.btree.user.domain.validator.RoleValidator;

import java.time.Instant;
import java.util.UUID;

public class Role extends Entity {

    private String name;
    private String description;
    private final Instant createdAt;

    private Role(final UUID id, final String name, final String description, final Instant createdAt) {
        super(id);
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Role create(final String name, final String description) {
        final var notification = Notification.create();
        final var role = new Role(UuidV7.generate(), name, description, Instant.now());
        role.validate(notification);
        if (notification.hasError()) {
            throw new DomainException(notification.getErrors());
        }
        return role;
    }

    public static Role with(final UUID id, final String name, final String description, final Instant createdAt) {
        return new Role(id, name, description, createdAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new RoleValidator(this, handler).validate();
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
}
