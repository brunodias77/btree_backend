package com.btree.api.controller;


import com.btree.api.dto.ApiResponse;
import com.btree.user.application.usecase.auth.register.RegisterUserInput;
import com.btree.user.application.usecase.auth.register.RegisterUserOutput;
import com.btree.user.application.usecase.auth.register.RegisterUserUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(final RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> register(
            @Valid @RequestBody final RegisterUserRequest request,
            final HttpServletRequest servletRequest
    ) {
        final var result = registerUserUseCase.execute(request.toInput());

        if (result.isLeft()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
                    ApiResponse.error(
                            HttpStatus.UNPROCESSABLE_CONTENT,
                            "Unprocessable Content",
                            result.getLeft(),
                            servletRequest.getRequestURI()
                    )
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        HttpStatus.CREATED,
                        "User registered successfully",
                        RegisterUserResponse.from(result.get()),
                        servletRequest.getRequestURI()
                )
        );
    }

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
        RegisterUserInput toInput() {
            return new RegisterUserInput(username, email, password);
        }
    }

    public record RegisterUserResponse(
            UUID id,
            String username,
            String email,
            boolean emailVerified,
            Instant createdAt
    ) {
        static RegisterUserResponse from(final RegisterUserOutput output) {
            return new RegisterUserResponse(
                    output.id(),
                    output.username(),
                    output.email(),
                    output.emailVerified(),
                    output.createdAt()
            );
        }
    }
}
