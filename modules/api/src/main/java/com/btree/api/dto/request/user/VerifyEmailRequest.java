package com.btree.api.dto.request.user;

import com.btree.user.application.usecase.auth.verify_email.VerifyEmailInput;
import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(
        @NotBlank
        String token
) {

    public VerifyEmailInput toInput() {
        return new VerifyEmailInput(token);
    }
}
