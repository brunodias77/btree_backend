package com.btree.api.mapper;

import com.btree.shared.validation.Error;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.error.UserError;
import org.springframework.http.HttpStatus;

public final class AuthHttpStatusMapper {

    private AuthHttpStatusMapper() {
    }

    public static HttpStatus register(final Notification notification) {
        if (hasError(notification, UserError.USERNAME_ALREADY_EXISTS)
                || hasError(notification, UserError.EMAIL_ALREADY_EXISTS)) {
            return HttpStatus.CONFLICT;
        }

        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    public static HttpStatus verifyEmail(final Notification notification) {
        if (hasError(notification, UserError.TOKEN_INVALID_TYPE)) {
            return HttpStatus.BAD_REQUEST;
        }

        if (hasError(notification, UserError.TOKEN_NOT_FOUND)
                || hasError(notification, UserError.USER_NOT_FOUND)) {
            return HttpStatus.NOT_FOUND;
        }

        if (hasError(notification, UserError.TOKEN_EXPIRED)) {
            return HttpStatus.GONE;
        }

        if (hasError(notification, UserError.TOKEN_ALREADY_USED)
                || hasError(notification, UserError.EMAIL_ALREADY_VERIFIED)) {
            return HttpStatus.CONFLICT;
        }

        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    public static HttpStatus login(final Notification notification) {
        if (hasError(notification, UserError.INVALID_CREDENTIALS)) {
            return HttpStatus.UNAUTHORIZED;
        }

        if (hasError(notification, UserError.ACCOUNT_DISABLED)
                || hasError(notification, UserError.ACCOUNT_LOCKED)) {
            return HttpStatus.FORBIDDEN;
        }

        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    private static boolean hasError(final Notification notification, final Error error) {
        return notification != null && notification.getErrors().contains(error);
    }
}
