package com.btree.shared.exception;

import com.btree.shared.validation.Error;

import java.util.Collections;
import java.util.List;

/**
 * Exceção de domínio lançada quando invariantes ou regras de negócio são violadas.
 *
 * Carrega a lista de erros de validação que causaram a falha, permitindo
 * que a camada de apresentação ou aplicação exiba todas as mensagens ao chamador.
 */
public class DomainException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Falha na validação de domínio";

    private final List<Error> errors;

    protected DomainException(final String message, final List<Error> errors) {
        super(message);
        this.errors = errors != null ? Collections.unmodifiableList(errors) : List.of();
    }

    public DomainException(final List<Error> errors) {
        this(errors != null && !errors.isEmpty() ? errors.get(0).message() : DEFAULT_MESSAGE, errors);
    }

    public static DomainException with(final Error error) {
        return new DomainException(List.of(error));
    }

    public static DomainException with(final List<Error> errors) {
        return new DomainException(errors);
    }

    public List<Error> getErrors() {
        return errors;
    }
}
