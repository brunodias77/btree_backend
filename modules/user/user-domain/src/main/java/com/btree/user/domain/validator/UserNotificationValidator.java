package com.btree.user.domain.validator;

import com.btree.shared.validation.ValidationHandler;
import com.btree.shared.validation.Validator;
import com.btree.user.domain.entity.UserNotification;
import com.btree.user.domain.error.UserNotificationError;

public class UserNotificationValidator extends Validator<UserNotification> {

    public UserNotificationValidator(final UserNotification notification, final ValidationHandler handler) {
        super(notification, handler);
    }

    @Override
    public void validate() {
        checkTitle();
        checkMessage();
        checkNotificationType();
    }

    private void checkTitle() {
        final var title = entity().getTitle();
        if (title == null || title.isBlank()) {
            validationHandler().append(UserNotificationError.TITLE_EMPTY);
        }
    }

    private void checkMessage() {
        final var message = entity().getMessage();
        if (message == null || message.isBlank()) {
            validationHandler().append(UserNotificationError.MESSAGE_EMPTY);
        }
    }

    private void checkNotificationType() {
        final var type = entity().getNotificationType();
        if (type == null || type.isBlank()) {
            validationHandler().append(UserNotificationError.NOTIFICATION_TYPE_EMPTY);
        }
    }
}
