package com.btree.user.application.usecase.auth.login;

import java.time.Instant;

public record LoginUserOutput(
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        String userId,
        String username,
        String email,
        java.util.List<String> roles,
        boolean requiresTwoFactor,
        String transactionId

) {
}
