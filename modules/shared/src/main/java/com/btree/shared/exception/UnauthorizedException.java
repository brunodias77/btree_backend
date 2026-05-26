package com.btree.shared.exception;

import com.btree.shared.validation.Error;

import java.util.List;

/**
 * Exceção lançada quando a autenticação falha ou as credenciais são inválidas (HTTP 401).
 * Exemplos: senha incorreta, token expirado, conta bloqueada.
 */
public class UnauthorizedException extends DomainException {

    protected UnauthorizedException(final String message) {
        super(List.of(new Error(message)));
    }

    public static UnauthorizedException with(final String message) {
        return new UnauthorizedException(message);
    }
}
