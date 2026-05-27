package com.btree.api.dto.response.user;

import com.btree.user.application.usecase.auth.login.LoginUserOutput;

import java.time.Instant;
import java.util.List;

public record LoginUserResponse(
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        String userId,
        String username,
        String email,
        List<String> roles,
        boolean requiresTwoFactor,
        String transactionId
) {

    public static LoginUserResponse from(final LoginUserOutput output) {
        return new LoginUserResponse(
                output.accessToken(),
                output.refreshToken(),
                output.accessTokenExpiresAt(),
                output.userId(),
                output.username(),
                output.email(),
                output.roles(),
                output.requiresTwoFactor(),
                output.transactionId()
        );
    }
}
