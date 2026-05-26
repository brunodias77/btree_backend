package com.btree.shared.validation;


/**
 * Classe base para todos os validadores de domínio.
 *
 * O parâmetro de tipo {@code T} torna explícito qual entidade está sendo validada,
 * evitando que subclasses passem a entidade de forma ad-hoc pelo construtor.
 * Subclasses implementam {@link #validate()} e acessam a entidade via {@link #entity()}
 * e o handler via {@link #validationHandler()}.
 */
public abstract class Validator<T> {

    private final T entity;
    private final ValidationHandler handler;

    protected Validator(final T entity, final ValidationHandler aHandler) {
        this.entity = entity;
        this.handler = aHandler;
    }

    /** Executa as regras de validação, reportando erros via {@link #validationHandler()}. */
    public abstract void validate();

    /** Retorna a entidade sendo validada. */
    protected T entity() {
        return this.entity;
    }

    /** Retorna o handler configurado para receber os erros desta validação. */
    protected ValidationHandler validationHandler() {
        return this.handler;
    }
}