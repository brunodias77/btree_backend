package com.btree.user.domain.entity;

import com.btree.shared.abstraction.Entity;
import com.btree.shared.exception.DomainException;
import com.btree.shared.util.UuidV7;
import com.btree.shared.validation.Notification;
import com.btree.shared.validation.ValidationHandler;
import com.btree.user.domain.validator.UserNotificationValidator;

import java.time.Instant;
import java.util.UUID;

public class UserNotification extends Entity {

    private final UUID userId;
    private String title;
    private String message;
    private String notificationType;
    private String referenceType;
    private UUID referenceId;
    private String actionUrl;
    private Instant readAt;
    private final Instant createdAt;

    private UserNotification(
            final UUID id, final UUID userId, final String title,
            final String message, final String notificationType,
            final String referenceType, final UUID referenceId,
            final String actionUrl, final Instant readAt, final Instant createdAt
    ) {
        super(id);
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.actionUrl = actionUrl;
        this.readAt = readAt;
        this.createdAt = createdAt;
    }

    public static UserNotification create(
            final UUID userId, final String title,
            final String message, final String notificationType
    ) {
        final var notification = Notification.create();
        final var userNotification = new UserNotification(
                UuidV7.generate(), userId, title, message,
                notificationType, null, null, null, null, Instant.now()
        );
        userNotification.validate(notification);
        if (notification.hasError()) {
            throw new DomainException(notification.getErrors());
        }
        return userNotification;
    }

    public static UserNotification with(
            final UUID id, final UUID userId, final String title,
            final String message, final String notificationType,
            final String referenceType, final UUID referenceId,
            final String actionUrl, final Instant readAt, final Instant createdAt
    ) {
        return new UserNotification(
                id, userId, title, message, notificationType,
                referenceType, referenceId, actionUrl, readAt, createdAt
        );
    }

    public void markAsRead() {
        if (this.readAt == null) {
            this.readAt = Instant.now();
        }
    }

    public boolean isRead() {
        return readAt != null;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new UserNotificationValidator(this, handler).validate();
    }

    public UUID getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getNotificationType() { return notificationType; }
    public String getReferenceType() { return referenceType; }
    public UUID getReferenceId() { return referenceId; }
    public String getActionUrl() { return actionUrl; }
    public Instant getReadAt() { return readAt; }
    public Instant getCreatedAt() { return createdAt; }
}
