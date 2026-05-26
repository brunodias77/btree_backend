package com.btree.user.domain.aggregate_root;

import com.btree.shared.abstraction.AggregateRoot;
import com.btree.shared.exception.DomainException;
import com.btree.shared.util.UuidV7;
import com.btree.shared.validation.Notification;
import com.btree.shared.validation.ValidationHandler;
import com.btree.user.domain.entity.NotificationPreference;
import com.btree.user.domain.entity.Profile;
import com.btree.user.domain.event.PasswordResetRequestedEvent;
import com.btree.user.domain.event.UserAccountLockedEvent;
import com.btree.user.domain.event.UserAccountUnlockedEvent;
import com.btree.user.domain.event.UserCreatedEvent;
import com.btree.user.domain.event.UserEmailChangedEvent;
import com.btree.user.domain.event.UserEmailVerifiedEvent;
import com.btree.user.domain.event.UserPasswordChangedEvent;
import com.btree.user.domain.event.UserTwoFactorDisabledEvent;
import com.btree.user.domain.event.UserTwoFactorEnabledEvent;
import com.btree.user.domain.validator.UserValidator;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends AggregateRoot {

    // ── Fields ───────────────────────────────────────────────────────────────

    private String username;
    private String email;
    private boolean emailVerified;
    private String passwordHash;
    private boolean requiresPassword = true;

    private String phoneNumber;
    private boolean phoneNumberVerified;

    private boolean twoFactorEnabled;
    private String twoFactorSecret;

    private boolean accountLocked;
    private Instant lockExpiresAt;
    private int accessFailedCount;

    private boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;

    private Profile profile;
    private NotificationPreference notificationPreference;
    private final Set<String> roles = new HashSet<>();

    // ── Constructor ──────────────────────────────────────────────────────────

    private User(
            final UUID id,
            final String username,
            final String email,
            final boolean emailVerified,
            final String passwordHash,
            final String phoneNumber,
            final boolean phoneNumberVerified,
            final boolean twoFactorEnabled,
            final String twoFactorSecret,
            final boolean accountLocked,
            final Instant lockExpiresAt,
            final int accessFailedCount,
            final boolean enabled,
            final Instant createdAt,
            final Instant updatedAt,
            final int version
    ) {
        super(id, version);
        this.username            = username;
        this.email               = email;
        this.emailVerified       = emailVerified;
        this.passwordHash        = passwordHash;
        this.phoneNumber         = phoneNumber;
        this.phoneNumberVerified = phoneNumberVerified;
        this.twoFactorEnabled    = twoFactorEnabled;
        this.twoFactorSecret     = twoFactorSecret;
        this.accountLocked       = accountLocked;
        this.lockExpiresAt       = lockExpiresAt;
        this.accessFailedCount   = accessFailedCount;
        this.enabled             = enabled;
        this.createdAt           = createdAt;
        this.updatedAt           = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static User create(
            final String username,
            final String email,
            final String passwordHash,
            final Notification notification
    ) {
        final var now = Instant.now();
        final var user = new User(
                UuidV7.generate(), username, email,
                false, passwordHash,
                null, false,
                false, null,
                false, null, 0, true,
                now, now, 1
        );
        user.requiresPassword       = true;
        user.profile                = Profile.create(user.getId());
        user.notificationPreference = NotificationPreference.create(user.getId());

        user.validate(notification);
        if (notification.hasError()) {
            throw new DomainException(notification.getErrors());
        }
        user.registerEvent(new UserCreatedEvent(user.getId().toString()));
        return user;
    }

    public static User createSocial(
            final String username,
            final String email
    ) {
        final var now = Instant.now();
        final var user = new User(
                UuidV7.generate(), username, email,
                true, null,
                null, false,
                false, null,
                false, null, 0, true,
                now, now, 1
        );
        user.requiresPassword = false;
        return user;
    }

    public static User createFromSocial(
            final String username,
            final String email,
            final Notification notification
    ) {
        final var now = Instant.now();
        final var user = new User(
                UuidV7.generate(), username, email,
                true, null,
                null, false,
                false, null,
                false, null, 0, true,
                now, now, 1
        );
        user.requiresPassword       = false;
        user.profile                = Profile.create(user.getId());
        user.notificationPreference = NotificationPreference.create(user.getId());

        user.validate(notification);
        if (notification.hasError()) {
            throw DomainException.with(notification.getErrors());
        }
        return user;
    }

    /** Reconstitui o agregado a partir da persistência. */
    public static User with(
            final UUID id,
            final String username,
            final String email,
            final boolean emailVerified,
            final String passwordHash,
            final String phoneNumber,
            final boolean phoneNumberVerified,
            final boolean twoFactorEnabled,
            final String twoFactorSecret,
            final boolean accountLocked,
            final Instant lockExpiresAt,
            final int accessFailedCount,
            final boolean enabled,
            final Instant createdAt,
            final Instant updatedAt,
            final int version
    ) {
        final var user = new User(
                id, username, email, emailVerified, passwordHash,
                phoneNumber, phoneNumberVerified, twoFactorEnabled, twoFactorSecret,
                accountLocked, lockExpiresAt, accessFailedCount, enabled, createdAt, updatedAt, version
        );
        user.requiresPassword = passwordHash != null;
        return user;
    }

    // ── Validation ───────────────────────────────────────────────────────────

    @Override
    public void validate(final ValidationHandler handler) {
        new UserValidator(this, handler).validate();
    }

    // ── Domain Behaviors: Identity ────────────────────────────────────────────

    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = Instant.now();
        incrementVersion();
        registerEvent(new UserEmailVerifiedEvent(getId().toString()));
    }

    public void changeEmail(final String newEmail) {
        this.email = newEmail;
        this.emailVerified = false;
        this.updatedAt = Instant.now();
        incrementVersion();
        registerEvent(new UserEmailChangedEvent(getId().toString(), newEmail));
    }

    public void changePassword(final String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = Instant.now();
        incrementVersion();
        registerEvent(new UserPasswordChangedEvent(getId().toString()));
    }

    public void requestPasswordReset(final String rawToken, final Instant expiresAt) {
        registerEvent(new PasswordResetRequestedEvent(getId().toString(), this.email, rawToken, expiresAt));
    }

    // ── Domain Behaviors: Phone ───────────────────────────────────────────────

    public void changePhoneNumber(final String newPhoneNumber) {
        this.phoneNumber = newPhoneNumber;
        this.phoneNumberVerified = false;
        this.updatedAt = Instant.now();
        incrementVersion();
    }

    public void verifyPhoneNumber() {
        this.phoneNumberVerified = true;
        this.updatedAt = Instant.now();
        incrementVersion();
    }

    // ── Domain Behaviors: Two-Factor ──────────────────────────────────────────

    public void enableTwoFactor(final String secret) {
        this.twoFactorEnabled = true;
        this.twoFactorSecret  = secret;
        this.updatedAt = Instant.now();
        incrementVersion();
        registerEvent(new UserTwoFactorEnabledEvent(getId().toString()));
    }

    public void disableTwoFactor() {
        this.twoFactorEnabled = false;
        this.twoFactorSecret  = null;
        this.updatedAt = Instant.now();
        incrementVersion();
        registerEvent(new UserTwoFactorDisabledEvent(getId().toString()));
    }

    // ── Domain Behaviors: Account Status ─────────────────────────────────────

    public void lockAccount(final Instant expiresAt) {
        this.accountLocked = true;
        this.lockExpiresAt = expiresAt;
        this.updatedAt = Instant.now();
        incrementVersion();
        registerEvent(new UserAccountLockedEvent(getId().toString(), expiresAt));
    }

    public void unlockAccount() {
        this.accountLocked     = false;
        this.lockExpiresAt     = null;
        this.accessFailedCount = 0;
        this.updatedAt = Instant.now();
        incrementVersion();
        registerEvent(new UserAccountUnlockedEvent(getId().toString()));
    }

    public void incrementAccessFailed() {
        this.accessFailedCount++;
        this.updatedAt = Instant.now();
        incrementVersion();
    }

    public void resetAccessFailed() {
        this.accessFailedCount = 0;
        this.updatedAt = Instant.now();
        incrementVersion();
    }

    public void enable() {
        this.enabled = true;
        this.updatedAt = Instant.now();
        incrementVersion();
    }

    public void disable() {
        this.enabled = false;
        this.updatedAt = Instant.now();
        incrementVersion();
    }

    // ── Domain Behaviors: Roles ───────────────────────────────────────────────

    public void addRole(final String role) {
        if (role != null && !role.isBlank()) {
            this.roles.add(role);
        }
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    public boolean isLockExpired() {
        return lockExpiresAt != null && Instant.now().isAfter(lockExpiresAt);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getUsername()                               { return username; }
    public String getEmail()                                  { return email; }
    public boolean isEmailVerified()                          { return emailVerified; }
    public String getPasswordHash()                           { return passwordHash; }
    public boolean isRequiresPassword()                       { return requiresPassword; }
    public String getPhoneNumber()                            { return phoneNumber; }
    public boolean isPhoneNumberVerified()                    { return phoneNumberVerified; }
    public boolean isTwoFactorEnabled()                       { return twoFactorEnabled; }
    public String getTwoFactorSecret()                        { return twoFactorSecret; }
    public boolean isAccountLocked()                          { return accountLocked; }
    public Instant getLockExpiresAt()                         { return lockExpiresAt; }
    public int getAccessFailedCount()                         { return accessFailedCount; }
    public boolean isEnabled()                                { return enabled; }
    public Instant getCreatedAt()                             { return createdAt; }
    public Instant getUpdatedAt()                             { return updatedAt; }
    public Profile getProfile()                               { return profile; }
    public NotificationPreference getNotificationPreference() { return notificationPreference; }
    public Set<String> getRoles()                             { return Collections.unmodifiableSet(roles); }
}
