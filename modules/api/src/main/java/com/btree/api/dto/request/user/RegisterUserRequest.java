package com.btree.api.dto.request.user;

import com.btree.user.application.usecase.auth.register.RegisterUserInput;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
            @NotBlank
            @Size(max = 256)
            String username,

            @NotBlank
            @Email
            @Size(max = 256)
            String email,

            @NotBlank
            @Size(min = 8)
            String password
    ) {
        public RegisterUserInput toInput() {
            return new RegisterUserInput(username, email, password);
        }
    }
