package com.btree.user.domain.validator;

import com.btree.shared.validation.Error;
import com.btree.shared.validation.ValidationHandler;
import com.btree.shared.validation.Validator;
import com.btree.user.domain.aggregate_root.Session;
import com.btree.user.domain.error.UserError;

public class SessionValidator extends Validator<Session> {

    public SessionValidator(final Session session, final ValidationHandler handler) {
        super(session, handler);
    }

    @Override
    public void validate() {
        checkUserId();
        checkRefreshTokenHash();
        checkExpiresAt();
    }

    private void checkUserId() {
        if (entity().getUserId() == null) {
            validationHandler().append(new Error("'userId' não pode ser nulo"));
        }
    }

    private void checkRefreshTokenHash() {
        final var hash = entity().getRefreshTokenHash();
        if (hash == null || hash.isBlank()) {
            validationHandler().append(UserError.SESSION_NOT_FOUND);
        }
    }

    private void checkExpiresAt() {
        if (entity().getExpiresAt() == null) {
            validationHandler().append(new Error("'expiresAt' não pode ser nulo"));
        }
    }
}
