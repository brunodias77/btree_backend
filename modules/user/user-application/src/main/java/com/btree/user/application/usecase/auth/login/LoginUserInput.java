package com.btree.user.application.usecase.auth.login;

public record LoginUserInput(String email, String password, String ipAddress, String userAgent) {
}
