package com.btree.api.dto.response.user;

import java.time.Instant;
import java.util.UUID;

import com.btree.user.application.usecase.auth.register.RegisterUserOutput;

public record RegisterUserResponse(
            UUID id,
            String username,
            String email,
            boolean emailVerified,
            Instant createdAt) {
        public static RegisterUserResponse from(final RegisterUserOutput output) {
            return new RegisterUserResponse(
                    output.id(),
                    output.username(),
                    output.email(),
                    output.emailVerified(),
                    output.createdAt());
        }
    }
