package com.btree.user.application.usecase.auth.verify_email;

import com.btree.shared.contract.TokenHasher;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.enums.TokenType;
import com.btree.shared.event.DomainEventPublisher;
import com.btree.shared.exception.DomainException;
import com.btree.shared.usecase.UnitUseCase;
import com.btree.shared.validation.Error;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.error.UserError;
import com.btree.user.domain.persistence.UserGateway;
import com.btree.user.domain.persistence.UserTokenGateway;
import io.vavr.control.Either;

public class VerifyEmailUseCase implements UnitUseCase<VerifyEmailInput> {

    private final UserTokenGateway userTokenGateway;
    private final UserGateway userGateway;
    private final TokenHasher tokenHasher;
    private final TransactionManager transactionManager;
    private final DomainEventPublisher domainEventPublisher;

    public VerifyEmailUseCase(
            final UserTokenGateway userTokenGateway,
            final UserGateway userGateway,
            final TokenHasher tokenHasher,
            final TransactionManager transactionManager,
            final DomainEventPublisher domainEventPublisher
    ) {
        this.userTokenGateway = userTokenGateway;
        this.userGateway = userGateway;
        this.tokenHasher = tokenHasher;
        this.transactionManager = transactionManager;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Either<Notification, Void> execute(final VerifyEmailInput input) {
        if (input == null) {
            return Either.left(Notification.create(new Error("'input' nao pode ser nulo")));
        }

        final var notification = Notification.create();
        final var rawToken = trim(input.token());

        if (!hasText(rawToken)) {
            notification.append(UserError.TOKEN_NOT_FOUND);
            return Either.left(notification);
        }

        final var tokenHash = tokenHasher.hash(rawToken);
        final var tokenOpt = userTokenGateway.findByTokenHash(tokenHash);

        if (tokenOpt.isEmpty()) {
            notification.append(UserError.TOKEN_NOT_FOUND);
            return Either.left(notification);
        }

        final var userToken = tokenOpt.get();

        if (!TokenType.EMAIL_VERIFICATION.name().equals(userToken.getTokenType())) {
            notification.append(UserError.TOKEN_INVALID_TYPE);
            return Either.left(notification);
        }

        if (userToken.isExpired()) {
            notification.append(UserError.TOKEN_EXPIRED);
            return Either.left(notification);
        }

        if (userToken.isUsed()) {
            notification.append(UserError.TOKEN_ALREADY_USED);
            return Either.left(notification);
        }

        final var userOpt = userGateway.findById(userToken.getUserId());

        if (userOpt.isEmpty()) {
            notification.append(UserError.USER_NOT_FOUND);
            return Either.left(notification);
        }

        final var user = userOpt.get();

        if (user.isEmailVerified()) {
            notification.append(UserError.EMAIL_ALREADY_VERIFIED);
            return Either.left(notification);
        }

        try {
            transactionManager.execute(() -> {
                userToken.markAsUsed();
                userTokenGateway.update(userToken);

                user.verifyEmail();
                userGateway.update(user);

                domainEventPublisher.publishAll(user.getDomainEvents());
                user.clearDomainEvents();

                return null;
            });

            return Either.right(null);
        } catch (final DomainException ex) {
            if (ex.getClass() != DomainException.class) {
                throw ex;
            }

            final var domainNotification = Notification.create();
            ex.getErrors().forEach(domainNotification::append);
            return Either.left(domainNotification);
        }
    }

    private static boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }

    private static String trim(final String value) {
        return value == null ? null : value.trim();
    }
}
