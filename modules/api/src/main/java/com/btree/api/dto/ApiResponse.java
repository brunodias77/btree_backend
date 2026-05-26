package com.btree.api.dto;

import com.btree.shared.validation.Notification;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        List<String> errors,
        Instant timestamp,
        String path
) {

    public static <T> ApiResponse<T> success(
            final HttpStatus status,
            final String message,
            final T data,
            final String path
    ) {
        return new ApiResponse<>(true, status.value(), message, data, null, Instant.now(), path);
    }

    public static <T> ApiResponse<T> error(
            final HttpStatus status,
            final String message,
            final String error,
            final String path
    ) {
        return error(status, message, List.of(error), path);
    }

    public static <T> ApiResponse<T> error(
            final HttpStatus status,
            final String message,
            final List<String> errors,
            final String path
    ) {
        return new ApiResponse<>(false, status.value(), message, null, List.copyOf(errors), Instant.now(), path);
    }

    public static <T> ApiResponse<T> error(
            final HttpStatus status,
            final String message,
            final Notification notification,
            final String path
    ) {
        final var errors = notification.getErrors().stream()
                .map(com.btree.shared.validation.Error::message)
                .toList();
        return error(status, message, errors, path);
    }
}
