package com.btree.user.application.usecase.auth.register;

import com.btree.shared.contract.PasswordHasher;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.event.DomainEventPublisher;
import com.btree.shared.exception.DomainException;
import com.btree.shared.usecase.UseCase;
import com.btree.shared.validation.Error;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.aggregate_root.User;
import com.btree.user.domain.error.UserError;
import com.btree.user.domain.persistence.UserGateway;
import com.btree.user.domain.validator.UserValidator;
import io.vavr.control.Either;

public class RegisterUserUseCase implements UseCase<RegisterUserInput, RegisterUserOutput> {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;
    private final DomainEventPublisher domainEventPublisher;
    private final TransactionManager transactionManager;

    public RegisterUserUseCase(
            final UserGateway userGateway,
            final PasswordHasher passwordHasher,
            final DomainEventPublisher domainEventPublisher,
            final TransactionManager transactionManager
    ) {
        this.userGateway = userGateway;
        this.passwordHasher = passwordHasher;
        this.domainEventPublisher = domainEventPublisher;
        this.transactionManager = transactionManager;
    }

    @Override
    public Either<Notification, RegisterUserOutput> execute(final RegisterUserInput input) {
        final var notification = Notification.create();

        if (input == null) {
            return Either.left(Notification.create(new Error("'input' não pode ser nulo")));
        }

        UserValidator.validatePassword(input.password(), notification);
        checkUniqueness(input, notification);

        if (notification.hasError()) {
            return Either.left(notification);
        }

        try {
            final var output = transactionManager.execute(() -> register(input));
            return Either.right(output);
        } catch (final DomainException ex) {
            final var domainNotification = Notification.create();
            ex.getErrors().forEach(domainNotification::append);
            return Either.left(domainNotification);
        } catch (final Exception ex) {
            return Either.left(Notification.create(ex));
        }
    }

    private void checkUniqueness(final RegisterUserInput input, final Notification notification) {
        final var username = trim(input.username());
        final var email = normalizeEmail(input.email());

        if (hasText(username) && userGateway.existsByUsername(username)) {
            notification.append(UserError.USERNAME_ALREADY_EXISTS);
        }

        if (hasText(email) && userGateway.existsByEmail(email)) {
            notification.append(UserError.EMAIL_ALREADY_EXISTS);
        }
    }

    private RegisterUserOutput register(final RegisterUserInput input) {
        final var username = trim(input.username());
        final var email = normalizeEmail(input.email());
        final var passwordHash = passwordHasher.hash(input.password());
        final var user = User.create(username, email, passwordHash, Notification.create());
        final var savedUser = userGateway.save(user);

        domainEventPublisher.publishAll(user.getDomainEvents());
        user.clearDomainEvents();

        return RegisterUserOutput.from(savedUser);
    }

    private static boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }

    private static String trim(final String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeEmail(final String value) {
        return value == null ? null : value.trim().toLowerCase();
    }
}
