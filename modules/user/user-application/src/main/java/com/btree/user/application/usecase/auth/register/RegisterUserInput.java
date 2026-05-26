package com.btree.user.application.usecase.auth.register;

public record RegisterUserInput(
        String username,
        String email,
        String password
) {
}
