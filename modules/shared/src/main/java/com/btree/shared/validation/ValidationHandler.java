package com.btree.shared.validation;


import java.util.List;


/**
 * Contrato para coleta e propagação de erros de validação.
 *
 * Existem duas implementações principais:
 * - {@link Notification}: acumula todos os erros sem interromper o fluxo.
 * - {@link ThrowsValidationHandler}: lança exceção imediatamente no primeiro erro.
 *
 * O {@link Validator} recebe uma instância deste handler e delega para ele
 * o comportamento de reporte de erros, desacoplando a lógica de validação
 * da estratégia de tratamento de falhas.
 */
public interface ValidationHandler {

    /** Adiciona um erro isolado ao handler. */
    ValidationHandler append(Error error);

    /** Mescla todos os erros de outro handler neste. */
    ValidationHandler append(ValidationHandler handler);

    /** Retorna a lista de erros coletados. */
    List<Error> getErrors();

    /** Retorna {@code true} se ao menos um erro foi registrado. */
    default boolean hasError() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    /** Retorna o primeiro erro registrado, ou {@code null} se não houver erros. */
    default Error firstError() {
        List<Error> errors = getErrors();
        return (errors != null && !errors.isEmpty()) ? errors.get(0) : null;
    }
}
