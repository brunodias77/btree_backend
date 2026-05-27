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

    private UseCaseResponse(final OUT output, final Notification notification) {
        this.output = output;
        this.notification = notification != null ? notification : Notification.create();
    }

    public static <OUT> UseCaseResponse<OUT> success(final OUT output) {
        return new UseCaseResponse<>(output, Notification.create());
    }

    public static UseCaseResponse<Void> success() {
        return success(null);
    }

    public static <OUT> UseCaseResponse<OUT> from(final Supplier<OUT> action) {
        try {
            return success(action.get());
        } catch (final Throwable throwable) {
            return failure(throwable);
        }
    }

    public static <OUT> UseCaseResponse<OUT> failure(final Error error) {
        return failure(Notification.create(error));
    }

    public static <OUT> UseCaseResponse<OUT> failure(final Notification notification) {
        return new UseCaseResponse<>(null, notification);
    }

    public static <OUT> UseCaseResponse<OUT> failure(final Throwable throwable) {
        if (throwable instanceof DomainException ex) {
            if (ex.getClass() != DomainException.class) {
                throw ex;
            }

            return failure(Notification.create().appendAll(ex.getErrors()));
        }

        return failure(Notification.create(throwable));
    }

    public boolean isSuccess() {
        return !hasError();
    }

    public boolean hasError() {
        return notification.hasError();
    }

    public OUT output() {
        return output;
    }

    public Notification notification() {
        return notification;
    }

    public List<Error> errors() {
        return notification.getErrors();
    }

    public Error firstError() {
        return notification.firstError();
    }
}
