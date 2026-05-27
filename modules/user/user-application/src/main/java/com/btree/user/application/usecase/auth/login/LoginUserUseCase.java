package com.btree.user.application.usecase.auth.login;

import com.btree.shared.contract.PasswordHasher;
import com.btree.shared.contract.TokenHasher;
import com.btree.shared.contract.TokenProvider;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.enums.TokenType;
import com.btree.shared.event.DomainEventPublisher;
import com.btree.shared.exception.DomainException;
import com.btree.shared.usecase.UseCase;
import com.btree.shared.validation.Error;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.aggregate_root.Session;
import com.btree.user.domain.aggregate_root.User;
import com.btree.user.domain.entity.LoginHistory;
import com.btree.user.domain.entity.UserToken;
import com.btree.user.domain.error.UserError;
import com.btree.user.domain.persistence.LoginHistoryGateway;
import com.btree.user.domain.persistence.SessionGateway;
import com.btree.user.domain.persistence.UserGateway;
import com.btree.user.domain.persistence.UserTokenGateway;
import com.btree.user.domain.value_object.DeviceInfo;
import io.vavr.control.Either;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LoginUserUseCase implements UseCase<LoginUserInput, LoginUserOutput> {

    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final long LOCK_DURATION_MINUTES = 15L;
    private static final long TWO_FACTOR_TOKEN_EXPIRATION_MINUTES = 5L;
    private static final String LOCAL_LOGIN_PROVIDER = "LOCAL";

    private final UserGateway userGateway;
    private final SessionGateway sessionGateway;
    private final UserTokenGateway userTokenGateway;
    private final LoginHistoryGateway loginHistoryGateway;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;
    private final TokenHasher tokenHasher;
    private final TransactionManager transactionManager;
    private final DomainEventPublisher eventPublisher;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public LoginUserUseCase(
            final UserGateway userGateway,
            final SessionGateway sessionGateway,
            final UserTokenGateway userTokenGateway,
            final LoginHistoryGateway loginHistoryGateway,
            final PasswordHasher passwordHasher,
            final TokenProvider tokenProvider,
            final TokenHasher tokenHasher,
            final TransactionManager transactionManager,
            final DomainEventPublisher eventPublisher,
            final long accessTokenExpirationMs,
            final long refreshTokenExpirationMs
    ) {
        this.userGateway = userGateway;
        this.sessionGateway = sessionGateway;
        this.userTokenGateway = userTokenGateway;
        this.loginHistoryGateway = loginHistoryGateway;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
        this.tokenHasher = tokenHasher;
        this.transactionManager = transactionManager;
        this.eventPublisher = eventPublisher;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public Either<Notification, LoginUserOutput> execute(final LoginUserInput input) {
        final var notification = Notification.create();

        if (input == null) {
            return Either.left(Notification.create(new Error("'input' não pode ser nulo")));
        }

        final var deviceInfo = DeviceInfo.of(input.ipAddress(), input.userAgent());
        final var email = normalizeEmail(input.email());

        final var userOpt = this.userGateway.findByEmail(email);
        if (userOpt.isEmpty()) {
            recordFailedAttempt((UUID) null, deviceInfo, "Usuário não encontrado");
            notification.append(UserError.INVALID_CREDENTIALS);
            return Either.left(notification);
        }

        final var user = userOpt.get();

        if (!user.isEnabled()) {
            recordFailedAttempt(user.getId(), deviceInfo, "Conta desativada");
            notification.append(UserError.ACCOUNT_DISABLED);
            return Either.left(notification);
        }

        if (user.isAccountLocked()) {
            // auto-unlock se o prazo de bloqueio ja expirou
            if (user.getLockExpiresAt() != null && Instant.now().isAfter(user.getLockExpiresAt())) {
                user.unlockAccount();
                persistUserBestEffort(user);
            } else {
                recordFailedAttempt(user.getId(), deviceInfo, "Conta bloqueada");
                notification.append(UserError.ACCOUNT_LOCKED);
                return Either.left(notification);
            }
        }

        if (!this.passwordHasher.matches(input.password(), user.getPasswordHash())) {
            recordFailedAttempt(user, deviceInfo, "Senha inválida");
            notification.append(UserError.INVALID_CREDENTIALS);
            return Either.left(notification);
        }

        // zerar contador de falhas em caso de sucesso
        if (user.getAccessFailedCount() > 0) {
            user.resetAccessFailed();
        }

        // se 2FA esta ativo, gerar token temporario em vez de emitir tokens finais
        if (user.isTwoFactorEnabled()) {
            return handleTwoFactorRequired(user, deviceInfo);
        }

        // gerar tokens finais
        final var now = Instant.now();
        final var accessTokenExpiresAt  = now.plusMillis(accessTokenExpirationMs);
        final var refreshTokenExpiresAt = now.plusMillis(refreshTokenExpirationMs);

        final var accessToken = this.tokenProvider.generate(
                user.getId().toString(),
                Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "roles", List.copyOf(user.getRoles())
                ),
                accessTokenExpiresAt
        );

        final var rawRefreshToken  = this.tokenHasher.generate();
        final var refreshTokenHash = this.tokenHasher.hash(rawRefreshToken);

        final var session = Session.create(
                user.getId(),
                refreshTokenHash,
                deviceInfo,
                refreshTokenExpiresAt
        );
        final var history = LoginHistory.recordSuccess(user.getId(), LOCAL_LOGIN_PROVIDER, deviceInfo);

        try {
            final var output = this.transactionManager.execute(() -> {
                this.userGateway.update(user); // persiste o reset do accessFailedCount
                this.sessionGateway.create(session);
                this.loginHistoryGateway.create(history);
                publishAndClearDomainEvents(user);
                return new LoginUserOutput(
                        accessToken,
                        rawRefreshToken,
                        accessTokenExpiresAt,
                        user.getId().toString(),
                        user.getUsername(),
                        user.getEmail(),
                        List.copyOf(user.getRoles()),
                        false,
                        null
                );
            });
            return Either.right(output);
        } catch (final DomainException ex) {
            if (ex.getClass() != DomainException.class) {
                throw ex;
            }

            return Either.left(toNotification(ex));
        }
    }

    /** Persiste o usuário sem lançar exceção (usado para desbloqueio automático). */
    private void persistUserBestEffort(final User user) {
        try {
            this.transactionManager.execute(() -> {
                this.userGateway.update(user);
                publishAndClearDomainEvents(user);
                return null;
            });
        } catch (Exception ignored) {
        }
    }

    /**
     * Incrementa o contador de falhas, bloqueia a conta se o limiar for atingido e
     * persiste ambas as alterações atomicamente (best-effort: não propaga exceções).
     */
    private void recordFailedAttempt(final User user, final DeviceInfo deviceInfo, final String reason) {
        if (user == null) {
            recordFailedAttempt((UUID) null, deviceInfo, reason);
            return;
        }

        user.incrementAccessFailed();
        if (user.getAccessFailedCount() >= MAX_FAILED_ATTEMPTS) {
            user.lockAccount(Instant.now().plus(LOCK_DURATION_MINUTES, ChronoUnit.MINUTES));
        }
        try {
            this.transactionManager.execute(() -> {
                this.userGateway.update(user);
                this.loginHistoryGateway.create(
                        LoginHistory.recordFailure(user.getId(), LOCAL_LOGIN_PROVIDER, deviceInfo, reason)
                );
                publishAndClearDomainEvents(user);
                return null;
            });
        } catch (Exception ignored) {
            // Best-effort: o registro de falha não deve impedir a resposta de credencial inválida
        }
    }

    private void recordFailedAttempt(final UUID userId, final DeviceInfo deviceInfo, final String reason) {
        try {
            this.transactionManager.execute(() -> {
                this.loginHistoryGateway.create(
                        LoginHistory.recordFailure(userId, LOCAL_LOGIN_PROVIDER, deviceInfo, reason)
                );
                return null;
            });
        } catch (Exception ignored) {
            // Best-effort: o registro de falha não deve impedir a resposta de autenticação
        }
    }

    // ── 2FA ──────────────────────────────────────────────────

    private Either<Notification, LoginUserOutput> handleTwoFactorRequired(
            final User user,
            final DeviceInfo deviceInfo
    ) {
        final var expiresAt = Instant.now().plus(TWO_FACTOR_TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES);
        final var transactionId = UUID.randomUUID().toString();
        final var tokenHash = this.tokenHasher.hash(transactionId);
        final var twoFactorToken = UserToken.create(
                user.getId(),
                TokenType.TWO_FACTOR.name(),
                tokenHash,
                expiresAt
        );

        final var username = user.getUsername();
        final var email = user.getEmail();

        try {
            final var output = this.transactionManager.execute(() -> {
                this.userGateway.update(user);  // persiste reset do accessFailedCount
                this.userTokenGateway.create(twoFactorToken);
                publishAndClearDomainEvents(user);
                return new LoginUserOutput(
                        null,
                        null,
                        null,
                        user.getId().toString(),
                        username,
                        email,
                        List.copyOf(user.getRoles()),
                        true,
                        transactionId
                );
            });
            return Either.right(output);
        } catch (final DomainException ex) {
            if (ex.getClass() != DomainException.class) {
                throw ex;
            }

            return Either.left(toNotification(ex));
        }
    }

    private void publishAndClearDomainEvents(final User user) {
        if (!user.getDomainEvents().isEmpty()) {
            this.eventPublisher.publishAll(user.getDomainEvents());
            user.clearDomainEvents();
        }
    }

    private static Notification toNotification(final DomainException ex) {
        final var notification = Notification.create();
        ex.getErrors().forEach(notification::append);
        return notification;
    }

    private static String normalizeEmail(final String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
