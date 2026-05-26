package com.btree.user.application.usecase.auth.register;

import com.btree.user.domain.aggregate_root.User;

import java.time.Instant;
import java.util.UUID;

public record RegisterUserOutput(
        UUID id,
        String username,
        String email,
        boolean emailVerified,
        Instant createdAt
) {

    public static RegisterUserOutput from(final User user) {
        return new RegisterUserOutput(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}
