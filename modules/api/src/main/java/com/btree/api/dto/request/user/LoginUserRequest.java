package com.btree.api.dto.request.user;

import com.btree.user.application.usecase.auth.login.LoginUserInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginUserRequest(
        @NotBlank
        @Email
        @Size(max = 256)
        String email,

        @NotBlank
        String password
) {

    public LoginUserInput toInput(final String ipAddress, final String userAgent) {
        return new LoginUserInput(email, password, ipAddress, userAgent);
    }
}
