package com.btree.shared.exception;

import com.btree.shared.validation.Error;

import java.util.List;

/**
 * Exceção lançada quando o usuário está autenticado mas não tem permissão para a operação (HTTP 403).
 * Exemplos: acesso a recurso de outro usuário, ação que exige role ausente.
 */
public class ForbiddenException extends DomainException {

    protected ForbiddenException(final String message) {
        super(List.of(new Error(message)));
    }

    public static ForbiddenException with(final String message) {
        return new ForbiddenException(message);
    }
}
