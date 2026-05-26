package com.btree.shared.abstraction;

import com.btree.shared.validation.ValidationHandler;

import java.util.Objects;
import java.util.UUID;


/**
 * Classe base para todas as Entidades de domínio.
 *
 * Uma Entidade é identificada pela sua identidade ({@code id}), não pelos seus atributos —
 * dois objetos com o mesmo {@code id} representam a mesma entidade mesmo que seus campos difiram.
 * Isso contrasta com {@link ValueObject}, cuja igualdade é estrutural (baseada nos valores).
 *
 * Toda subclasse deve implementar {@link #validate(ValidationHandler)} para declarar
 * e verificar seus invariantes de domínio.
 */
public abstract class Entity{

    protected final UUID id;

    protected Entity(final UUID id) {
        Objects.requireNonNull(id, "'id' não pode ser nulo");
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    /**
     * Verifica os invariantes desta entidade, reportando erros via {@code handler}.
     *
     * <p>O comportamento ao encontrar um erro depende da implementação de
     * {@link ValidationHandler} recebida: {@link com.btree.shared.validation.Notification}
     * acumula todos os erros; {@link com.btree.shared.validation.ThrowsValidationHandler}
     * interrompe na primeira falha.
     *
     * @param handler handler para coletar ou lançar os erros encontrados
     */
    public abstract void validate(ValidationHandler handler);

    /** Igualdade baseada exclusivamente no {@code id} — dois objetos do mesmo tipo e id são a mesma entidade. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + "}";
    }
}
