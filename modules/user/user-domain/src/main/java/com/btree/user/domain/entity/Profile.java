package com.btree.user.domain.entity;

import com.btree.shared.abstraction.Entity;
import com.btree.shared.util.UuidV7;
import com.btree.shared.validation.ValidationHandler;
import com.btree.user.domain.validator.ProfileValidator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Profile extends Entity {

    private final UUID userId;
    private String firstName;
    private String lastName;
    private String displayName;
    private String avatarUrl;
    private LocalDate birthDate;
    private String gender;
    private String cpf;
    private String preferredLanguage;
    private String preferredCurrency;
    private boolean newsletterSubscribed;
    private Instant acceptedTermsAt;
    private Instant acceptedPrivacyAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Profile(
            final UUID id,
            final UUID userId,
            final String firstName,
            final String lastName,
            final String displayName,
            final String avatarUrl,
            final LocalDate birthDate,
            final String gender,
            final String cpf,
            final String preferredLanguage,
            final String preferredCurrency,
            final boolean newsletterSubscribed,
            final Instant acceptedTermsAt,
            final Instant acceptedPrivacyAt,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        super(id);
        this.userId               = userId;
        this.firstName            = firstName;
        this.lastName             = lastName;
        this.displayName          = displayName;
        this.avatarUrl            = avatarUrl;
        this.birthDate            = birthDate;
        this.gender               = gender;
        this.cpf                  = cpf;
        this.preferredLanguage    = preferredLanguage;
        this.preferredCurrency    = preferredCurrency;
        this.newsletterSubscribed = newsletterSubscribed;
        this.acceptedTermsAt      = acceptedTermsAt;
        this.acceptedPrivacyAt    = acceptedPrivacyAt;
        this.createdAt            = createdAt;
        this.updatedAt            = updatedAt;
        this.deletedAt            = deletedAt;
    }

    public static Profile create(final UUID userId) {
        final var now = Instant.now();
        return new Profile(
                UuidV7.generate(), userId,
                null, null, null, null,
                null, null, null,
                "pt-BR", "BRL", false,
                null, null,
                now, now, null
        );
    }

    public static Profile with(
            final UUID id,
            final UUID userId,
            final String firstName,
            final String lastName,
            final String displayName,
            final String avatarUrl,
            final LocalDate birthDate,
            final String gender,
            final String cpf,
            final String preferredLanguage,
            final String preferredCurrency,
            final boolean newsletterSubscribed,
            final Instant acceptedTermsAt,
            final Instant acceptedPrivacyAt,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        return new Profile(
                id, userId, firstName, lastName, displayName, avatarUrl,
                birthDate, gender, cpf, preferredLanguage, preferredCurrency,
                newsletterSubscribed, acceptedTermsAt, acceptedPrivacyAt,
                createdAt, updatedAt, deletedAt
        );
    }

    public void update(
            final String firstName,
            final String lastName,
            final String avatarUrl
    ) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.avatarUrl = avatarUrl;
        this.updatedAt = Instant.now();
    }

    public void updatePersonalData(
            final String firstName,
            final String lastName,
            final String cpf,
            final LocalDate birthDate,
            final String gender,
            final String preferredLanguage,
            final String preferredCurrency,
            final boolean newsletterSubscribed
    ) {
        this.firstName            = firstName;
        this.lastName             = lastName;
        this.displayName          = buildDisplayName(firstName, lastName);
        this.cpf                  = cpf;
        this.birthDate            = birthDate;
        this.gender               = gender;
        this.preferredLanguage    = preferredLanguage != null ? preferredLanguage : "pt-BR";
        this.preferredCurrency    = preferredCurrency != null ? preferredCurrency : "BRL";
        this.newsletterSubscribed = newsletterSubscribed;
        this.updatedAt            = Instant.now();
    }

    private static String buildDisplayName(final String firstName, final String lastName) {
        if (firstName == null && lastName == null) return null;
        final String first = firstName != null ? firstName.trim() : "";
        final String last  = lastName  != null ? " " + lastName.trim() : "";
        return (first + last).trim();
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new ProfileValidator(this, handler).validate();
    }

    public UUID getUserId()                  { return userId; }
    public String getFirstName()             { return firstName; }
    public String getLastName()              { return lastName; }
    public String getDisplayName()           { return displayName; }
    public String getAvatarUrl()             { return avatarUrl; }
    public LocalDate getBirthDate()          { return birthDate; }
    public String getGender()                { return gender; }
    public String getCpf()                   { return cpf; }
    public String getPreferredLanguage()     { return preferredLanguage; }
    public String getPreferredCurrency()     { return preferredCurrency; }
    public boolean isNewsletterSubscribed()  { return newsletterSubscribed; }
    public Instant getAcceptedTermsAt()      { return acceptedTermsAt; }
    public Instant getAcceptedPrivacyAt()    { return acceptedPrivacyAt; }
    public Instant getCreatedAt()            { return createdAt; }
    public Instant getUpdatedAt()            { return updatedAt; }
    public Instant getDeletedAt()            { return deletedAt; }
}
