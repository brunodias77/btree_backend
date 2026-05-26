package com.btree.shared.validation;



import com.btree.shared.exception.DomainException;

import java.util.List;


/**
 * Implementação de {@link ValidationHandler} que lança {@link DomainException}
 * imediatamente ao receber qualquer erro de validação.
 *
 * É stateless, portanto exposta como singleton via {@link #INSTANCE}.
 * Use quando o primeiro erro deve interromper o fluxo de imediato (fail-fast),
 * em vez de acumular todos os erros como faz {@link Notification}.
 */
public class ThrowsValidationHandler implements ValidationHandler {

    public static final ThrowsValidationHandler INSTANCE = new ThrowsValidationHandler();

    private ThrowsValidationHandler() {}

    /** Lança {@link DomainException} com o erro recebido. */
    @Override
    public ValidationHandler append(Error error) {
        throw new DomainException(List.of(error));
    }

    /** Lança {@link DomainException} com todos os erros do handler recebido. */
    @Override
    public ValidationHandler append(ValidationHandler handler) {
        throw new DomainException(handler.getErrors());
    }

    /** Sempre retorna lista vazia — este handler nunca acumula erros. */
    @Override
    public List<Error> getErrors() { return List.of(); }
}
