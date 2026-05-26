package com.btree.user.domain.validator;

import com.btree.shared.validation.ValidationHandler;
import com.btree.shared.validation.Validator;
import com.btree.user.domain.entity.Role;
import com.btree.user.domain.error.RoleError;

public class RoleValidator extends Validator<Role> {

    public RoleValidator(final Role role, final ValidationHandler handler) {
        super(role, handler);
    }

    @Override
    public void validate() {
        checkName();
    }

    private void checkName() {
        final var name = entity().getName();
        if (name == null || name.isBlank()) {
            validationHandler().append(RoleError.NAME_EMPTY);
        }
    }
}
