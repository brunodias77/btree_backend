package com.btree.user.application.usecase.auth.register;

import com.btree.shared.contract.EmailService;
import com.btree.shared.contract.PasswordHasher;
import com.btree.shared.contract.TokenHasher;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.event.DomainEventPublisher;
import com.btree.shared.event.IntegrationEventPublisher;
import com.btree.shared.event.user.UserRegisteredIntegrationEvent;
import com.btree.shared.enums.TokenType;
import com.btree.shared.usecase.UseCase;
import com.btree.shared.usecase.UseCaseResponse;
import com.btree.shared.validation.Error;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.aggregate_root.User;
import com.btree.user.domain.entity.UserToken;
import com.btree.user.domain.error.UserError;
import com.btree.user.domain.persistence.UserGateway;
import com.btree.user.domain.persistence.UserTokenGateway;
import com.btree.user.domain.validator.UserValidator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class RegisterUserUseCase implements UseCase<RegisterUserInput, RegisterUserOutput> {

    private static final long EMAIL_VERIFICATION_EXPIRATION_HOURS = 24L;
    private static final String DEFAULT_ROLE = "customer";
    private static final String DEFAULT_ROLE_DESCRIPTION = "Default customer role";

    private final UserGateway userGateway;
    private final UserTokenGateway userTokenGateway;
    private final PasswordHasher passwordHasher;
    private final TokenHasher tokenHasher;
    private final DomainEventPublisher domainEventPublisher;
    private final IntegrationEventPublisher integrationEventPublisher;
    private final TransactionManager transactionManager;
    private final EmailService emailService;

    public RegisterUserUseCase(
            final UserGateway userGateway,
            final UserTokenGateway userTokenGateway,
            final PasswordHasher passwordHasher,
            final TokenHasher tokenHasher,
            final DomainEventPublisher domainEventPublisher,
            final IntegrationEventPublisher integrationEventPublisher,
            final TransactionManager transactionManager,
            final EmailService emailService
    ) {
        this.userGateway = userGateway;
        this.userTokenGateway = userTokenGateway;
        this.passwordHasher = passwordHasher;
        this.tokenHasher = tokenHasher;
        this.domainEventPublisher = domainEventPublisher;
        this.integrationEventPublisher = integrationEventPublisher;
        this.transactionManager = transactionManager;
        this.emailService = emailService;
    }

    @Override
    public UseCaseResponse<RegisterUserOutput> execute(final RegisterUserInput input) {
        final var notification = Notification.create();

        if (input == null) {
            return UseCaseResponse.failure(new Error("'input' não pode ser nulo"));
        }

        UserValidator.validatePassword(input.password(), notification);
        checkUniqueness(input, notification);

        if (notification.hasError()) {
            return UseCaseResponse.failure(notification);
        }

        return UseCaseResponse.from(() -> transactionManager.execute(() -> register(input)));
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
        final var username = normalizeUsername(input.username());
        final var email = normalizeEmail(input.email());
        final var passwordHash = passwordHasher.hash(input.password());
        final var user = User.create(username, email, passwordHash, Notification.create());
        final var rawEmailVerificationToken = tokenHasher.generate();
        final var emailVerificationToken = UserToken.create(
                user.getId(),
                TokenType.EMAIL_VERIFICATION.name(),
                tokenHasher.hash(rawEmailVerificationToken),
                Instant.now().plus(EMAIL_VERIFICATION_EXPIRATION_HOURS, ChronoUnit.HOURS)
        );

        userGateway.createRoleIfNotExists(DEFAULT_ROLE, DEFAULT_ROLE_DESCRIPTION);
        final var savedUser = userGateway.save(user);
        userGateway.assignRole(savedUser.getId(), DEFAULT_ROLE);
        userTokenGateway.create(emailVerificationToken);

        domainEventPublisher.publishAll(user.getDomainEvents());
        user.clearDomainEvents();

        integrationEventPublisher.publish(new UserRegisteredIntegrationEvent(savedUser.getId(), savedUser.getEmail()));
        transactionManager.afterCommit(() ->
                emailService.sendEmailVerification(
                        savedUser.getEmail(),
                        savedUser.getUsername(),
                        rawEmailVerificationToken
                )
        );

        return RegisterUserOutput.from(savedUser);
    }

    private static boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }

    private static String trim(final String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeUsername(final String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private static String normalizeEmail(final String value) {
        return value == null ? null : value.trim().toLowerCase();
    }
}
