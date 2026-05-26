package com.btree.user.domain.validator;

import com.btree.shared.validation.ValidationHandler;
import com.btree.shared.validation.Validator;
import com.btree.shared.value_object.Cpf;
import com.btree.user.domain.entity.Profile;
import com.btree.user.domain.error.ProfileError;

public class ProfileValidator extends Validator<Profile> {

    private static final int NAME_MAX_LENGTH     = 100;
    private static final int LANGUAGE_MAX_LENGTH = 10;
    private static final int LANGUAGE_MIN_LENGTH = 2;
    private static final int CURRENCY_LENGTH     = 3;

    public ProfileValidator(final Profile profile, final ValidationHandler handler) {
        super(profile, handler);
    }

    @Override
    public void validate() {
        checkFirstName();
        checkLastName();
        checkCpf();
        checkPreferredLanguage();
        checkPreferredCurrency();
    }

    private void checkFirstName() {
        final var firstName = entity().getFirstName();
        if (firstName != null && firstName.length() > NAME_MAX_LENGTH) {
            validationHandler().append(ProfileError.NAME_TOO_LONG);
        }
    }

    private void checkLastName() {
        final var lastName = entity().getLastName();
        if (lastName != null && lastName.length() > NAME_MAX_LENGTH) {
            validationHandler().append(ProfileError.NAME_TOO_LONG);
        }
    }

    private void checkCpf() {
        final var cpf = entity().getCpf();
        if (cpf != null && !cpf.isBlank()) {
            try {
                Cpf.of(cpf);
            } catch (IllegalArgumentException e) {
                validationHandler().append(ProfileError.CPF_INVALID);
            }
        }
    }

    private void checkPreferredLanguage() {
        final var lang = entity().getPreferredLanguage();
        if (lang != null && (lang.length() < LANGUAGE_MIN_LENGTH || lang.length() > LANGUAGE_MAX_LENGTH)) {
            validationHandler().append(ProfileError.PREFERRED_LANGUAGE_INVALID);
        }
    }

    private void checkPreferredCurrency() {
        final var currency = entity().getPreferredCurrency();
        if (currency != null && currency.length() != CURRENCY_LENGTH) {
            validationHandler().append(ProfileError.PREFERRED_CURRENCY_INVALID);
        }
    }
}
