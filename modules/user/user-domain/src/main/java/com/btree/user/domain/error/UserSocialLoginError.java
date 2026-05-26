package com.btree.user.domain.error;

public final class UserSocialLoginError {
    private UserSocialLoginError() {}

    public static final Error PROVIDER_EMPTY = new Error("'provider' não pode estar vazio");
    public static final Error PROVIDER_USER_ID_EMPTY = new Error("'providerUserId' não pode estar vazio");
}
