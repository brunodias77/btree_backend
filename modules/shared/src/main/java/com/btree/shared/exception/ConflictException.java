package com.btree.shared.exception;

import com.btree.shared.validation.Error;

import java.util.List;

/**
 * Exceção lançada quando uma operação viola uma restrição de unicidade ou estado (HTTP 409).
 * Exemplos: username/email duplicados, tentativa de confirmar um pedido já cancelado.
 */
public class ConflictException extends DomainException {

    protected ConflictException(final String message) {
        super(List.of(new Error(message)));
    }

    public static ConflictException with(final String message) {
        return new ConflictException(message);
    }

    public static ConflictException duplicated(final String field, final String value) {
        return new ConflictException("%s '%s' já está em uso".formatted(field, value));
    }
}
