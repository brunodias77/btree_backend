package com.btree.api.controller;

import com.btree.api.dto.ApiResponse;
import com.btree.api.dto.request.user.RegisterUserRequest;
import com.btree.api.dto.request.user.VerifyEmailRequest;
import com.btree.api.dto.response.user.RegisterUserResponse;
import com.btree.api.mapper.AuthHttpStatusMapper;
import com.btree.user.application.usecase.auth.register.RegisterUserUseCase;
import com.btree.user.application.usecase.auth.verify_email.VerifyEmailUseCase;
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
    private final VerifyEmailUseCase verifyEmailUseCase;

    public AuthController(
            final RegisterUserUseCase registerUserUseCase,
            final VerifyEmailUseCase verifyEmailUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.verifyEmailUseCase = verifyEmailUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Cria um usuario customer e dispara a criacao do carrinho via outbox.")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> register(
            @Valid @RequestBody final RegisterUserRequest request,
            final HttpServletRequest servletRequest) {
        final var result = registerUserUseCase.execute(request.toInput());

        if (result.isLeft()) {
            final var status = AuthHttpStatusMapper.register(result.getLeft());

            return ResponseEntity.status(status).body(
                    ApiResponse.error(
                            status,
                            status.getReasonPhrase(),
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

    @PostMapping("/verify-email")
    @Operation(summary = "Verificar e-mail", description = "Valida o token enviado por e-mail e confirma o e-mail do usuario.")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody final VerifyEmailRequest request,
            final HttpServletRequest servletRequest) {
        final var result = verifyEmailUseCase.execute(request.toInput());

        if (result.isLeft()) {
            final var status = AuthHttpStatusMapper.verifyEmail(result.getLeft());

            return ResponseEntity.status(status).body(
                    ApiResponse.error(
                            status,
                            status.getReasonPhrase(),
                            result.getLeft(),
                            servletRequest.getRequestURI()));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "E-mail verificado com sucesso",
                        null,
                        servletRequest.getRequestURI()));
    }

}
