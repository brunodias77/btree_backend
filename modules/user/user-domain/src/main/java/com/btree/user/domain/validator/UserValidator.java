package com.btree.user.domain.validator;

import com.btree.shared.validation.Error;
import com.btree.shared.validation.ValidationHandler;
import com.btree.shared.validation.Validator;
import com.btree.user.domain.aggregate_root.User;

import java.util.regex.Pattern;

public class UserValidator extends Validator<User> {

    private static final int USERNAME_MAX_LENGTH = 256;
    private static final int EMAIL_MAX_LENGTH = 256;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PASSWORD_UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern PASSWORD_LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern PASSWORD_DIGIT = Pattern.compile("[0-9]");
    private static final Pattern PASSWORD_SPECIAL = Pattern.compile("[^a-zA-Z0-9]");

    public UserValidator(final User user, final ValidationHandler handler) {
        super(user, handler);
    }

    @Override
    public void validate() {
        checkUsername();
        checkEmail();
        checkPasswordHash();
    }

    private void checkUsername(){
        final var username = entity().getUsername();
        if (username == null || username.isBlank()) {
            this.validationHandler().append(new Error("'username' não pode ser nulo ou vazio"));
            return;
        }
        if (username.length() > USERNAME_MAX_LENGTH) {
            this.validationHandler().append(new Error("'username' não pode ter mais de " + USERNAME_MAX_LENGTH + " caracteres"));
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            this.validationHandler().append(new Error("'username' de formato inválido. Use apenas alfanuméricos, hífens e underlines."));
        }
    }

    private void checkEmail(){
        final var email = entity().getEmail();
        if (email == null || email.isBlank()) {
            this.validationHandler().append(new Error("'email' não pode ser nulo ou vazio"));
            return;
        }
        if (email.length() > EMAIL_MAX_LENGTH) {
            this.validationHandler().append(new Error("'email' não pode ter mais de " + EMAIL_MAX_LENGTH + " caracteres"));
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            this.validationHandler().append(new Error("'email' de formato inválido"));
        }
    }

    private void checkPasswordHash() {
        if (!entity().isRequiresPassword()) return;
        final var hash = entity().getPasswordHash();
        if (hash == null || hash.isBlank()) {
            this.validationHandler().append(new Error("'passwordHash' não pode ser nulo ou vazio"));
        }
    }

    /**
     * Método reutilizável para validação de senha
     */
    public static void validatePassword(
            final String pass,
            final ValidationHandler handler
    ) {
        if (pass == null || pass.isBlank()) {
            handler.append(new Error("'password' não pode ser nulo ou vazio"));
            return;
        }

        if (pass.length() < PASSWORD_MIN_LENGTH) {
            handler.append(new Error("'password' deve ter no mínimo " + PASSWORD_MIN_LENGTH + " caracteres"));
        }

        if (!PASSWORD_UPPERCASE.matcher(pass).find()) {
            handler.append(new Error("'password' deve possuir ao menos uma letra maiúscula"));
        }

        if (!PASSWORD_LOWERCASE.matcher(pass).find()) {
            handler.append(new Error("'password' deve possuir ao menos uma letra minúscula"));
        }

        if (!PASSWORD_DIGIT.matcher(pass).find()) {
            handler.append(new Error("'password' deve possuir ao menos um dígito numérico"));
        }

        if (!PASSWORD_SPECIAL.matcher(pass).find()) {
            handler.append(new Error("'password' deve possuir ao menos um caractere especial"));
        }
    }


}
