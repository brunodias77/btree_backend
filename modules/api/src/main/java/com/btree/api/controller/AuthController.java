package com.btree.api.controller;

import com.btree.api.dto.ApiResponse;
import com.btree.api.dto.request.user.RegisterUserRequest;
import com.btree.api.dto.response.user.RegisterUserResponse;
import com.btree.user.application.usecase.auth.register.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Endpoints de autenticacao e registro")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(final RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Cria um usuario customer e dispara a criacao do carrinho via outbox.")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> register(
            @Valid @RequestBody final RegisterUserRequest request,
            final HttpServletRequest servletRequest) {
        final var result = registerUserUseCase.execute(request.toInput());

        if (result.isLeft()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
                    ApiResponse.error(
                            HttpStatus.UNPROCESSABLE_CONTENT,
                            "Unprocessable Content",
                            result.getLeft(),
                            servletRequest.getRequestURI()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        HttpStatus.CREATED,
                        "Usuario registrado com sucesso",
                        RegisterUserResponse.from(result.get()),
                        servletRequest.getRequestURI()));
    }

}
