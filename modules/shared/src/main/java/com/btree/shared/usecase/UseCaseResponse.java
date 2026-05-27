package com.btree.shared.usecase;

import com.btree.shared.exception.DomainException;
import com.btree.shared.validation.Error;
import com.btree.shared.validation.Notification;

import java.util.List;
import java.util.function.Supplier;

/**
 * Resposta padrao para casos de uso de escrita.
 *
 * <p>Carrega o output no sucesso ou os erros acumulados em uma {@link Notification}
 * quando alguma validacao/regra de negocio falhar.
 *
 * @param <OUT> tipo do output retornado pelo caso de uso
 */
public final class UseCaseResponse<OUT> {

    private final OUT output;
    private final Notification notification;
    private final boolean success;

    private UseCaseResponse(final OUT output, final Notification notification, final boolean success) {
        this.output = output;
        this.notification = notification;
        this.success = success;
    }

    public static <OUT> UseCaseResponse<OUT> success(final OUT output) {
        return new UseCaseResponse<>(output, null, true);
    }

    public static <OUT> UseCaseResponse<OUT> failure(final Notification notification) {
        return new UseCaseResponse<>(null, notification, false);
    }

    public static <OUT> UseCaseResponse<OUT> execute(final Supplier<OUT> action) {
        try {
            return success(action.get());
        } catch (final DomainException ex) {
            return failure(Notification.create().appendAll(ex.getErrors()));
        }
    }

    public static UseCaseResponse<Void> executeVoid(final Runnable action) {
        try {
            action.run();
            return success(null);
        } catch (final DomainException ex) {
            return failure(Notification.create().appendAll(ex.getErrors()));
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public OUT getOutput() {
        return output;
    }

    public List<Error> getErrors() {
        return notification != null ? notification.getErrors() : List.of();
    }
}
