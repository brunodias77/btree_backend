package com.btree.api.exception;

import com.btree.api.dto.ApiResponse;
import com.btree.shared.exception.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.btree.shared.exception.DomainException;
import com.btree.shared.exception.ExternalServiceException;
import com.btree.shared.exception.ForbiddenException;
import com.btree.shared.exception.NotFoundException;
import com.btree.shared.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpServletRequest request
    ) {
        final var messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> "'" + fe.getField() + "' " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(
                ApiResponse.error(HttpStatus.BAD_REQUEST, "Bad Request", messages, request.getRequestURI())
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
            final UnauthorizedException ex,
            final HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getErrors().get(0).message(), request.getRequestURI())
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(
            final ForbiddenException ex,
            final HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.error(HttpStatus.FORBIDDEN, "Forbidden", ex.getErrors().get(0).message(), request.getRequestURI())
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(
            final ConflictException ex,
            final HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error(HttpStatus.CONFLICT, "Conflict", ex.getErrors().get(0).message(), request.getRequestURI())
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            final NotFoundException ex,
            final HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(HttpStatus.NOT_FOUND, "Not Found", ex.getErrors().get(0).message(), request.getRequestURI())
        );
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleExternalService(
            final ExternalServiceException ex,
            final HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                ApiResponse.error(HttpStatus.BAD_GATEWAY, "Bad Gateway", "Serviço externo indisponível: " + ex.getServiceName(), request.getRequestURI())
        );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomain(
            final DomainException ex,
            final HttpServletRequest request
    ) {
        final var messages = ex.getErrors().stream().map(e -> e.message()).toList();
        return ResponseEntity.unprocessableEntity().body(
                ApiResponse.error(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", messages, request.getRequestURI())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(
            final Exception ex,
            final HttpServletRequest request
    ) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return ResponseEntity.internalServerError().body(
                ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Ocorreu um erro inesperado", request.getRequestURI())
        );
    }
}
