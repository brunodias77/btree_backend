package com.btree.shared.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementação de {@link ValidationHandler} que acumula todos os erros
 * de validação sem interromper o fluxo de execução.
 *
 * Preferível ao {@link ThrowsValidationHandler} quando é necessário coletar
 * todos os erros de uma entidade de uma só vez antes de retornar ao chamador,
 * permitindo que o cliente exiba todas as falhas simultaneamente.
 */
public class Notification implements ValidationHandler {

    private final List<Error> errors = new ArrayList<>();

    /** Cria uma Notification vazia. */
    public static Notification create() { return new Notification(); }

    /** Cria uma Notification já com o erro inicial informado. */
    public static Notification create(Error error) {
        return new Notification().append(error);
    }

    /** Cria uma Notification a partir da mensagem de uma exceção lançada. */
    public static Notification create(final Throwable t) {
        return create(new Error(t.getMessage()));
    }

    /** Adiciona o erro à lista interna; ignora silenciosamente valores nulos. */
    @Override
    public Notification append(Error error) {
        if (error != null) {
            this.errors.add(error);
        }
        return this;
    }

    /** Mescla todos os erros de outro handler nesta Notification; ignora handler nulo. */
    @Override
    public Notification append(ValidationHandler handler) {
        if (handler != null && handler.getErrors() != null) {
            this.errors.addAll(handler.getErrors());
        }
        return this;
    }

    /** Adiciona todos os erros informados; ignora listas nulas. */
    public Notification appendAll(final List<Error> errors) {
        if (errors != null) {
            errors.forEach(this::append);
        }
        return this;
    }

    /** Retorna uma visão não-modificável da lista de erros acumulados. */
    @Override
    public List<Error> getErrors() { return Collections.unmodifiableList(this.errors); }
}
