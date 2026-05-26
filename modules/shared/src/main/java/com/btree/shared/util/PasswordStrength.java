package com.btree.shared.util;

import java.util.regex.Pattern;

public final class PasswordStrength {

    private static final Pattern STRONG_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$"
    );

    private PasswordStrength() {}

    public static boolean isStrong(final String password) {
        return password != null && STRONG_PATTERN.matcher(password).matches();
    }
}
