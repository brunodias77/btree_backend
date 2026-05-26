package com.btree.user.domain.entity;

import com.btree.shared.abstraction.Entity;
import com.btree.shared.util.UuidV7;
import com.btree.shared.validation.ValidationHandler;

import java.time.Instant;
import java.util.UUID;

public class NotificationPreference extends Entity {

    private final UUID userId;
    private boolean emailEnabled;
    private boolean pushEnabled;
    private boolean smsEnabled;
    private boolean orderUpdates;
    private boolean promotions;
    private boolean priceDrops;
    private boolean backInStock;
    private boolean newsletter;
    private final Instant createdAt;
    private Instant updatedAt;

    private NotificationPreference(
            final UUID id,
            final UUID userId,
            final boolean emailEnabled,
            final boolean pushEnabled,
            final boolean smsEnabled,
            final boolean orderUpdates,
            final boolean promotions,
            final boolean priceDrops,
            final boolean backInStock,
            final boolean newsletter,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        super(id);
        this.userId       = userId;
        this.emailEnabled = emailEnabled;
        this.pushEnabled  = pushEnabled;
        this.smsEnabled   = smsEnabled;
        this.orderUpdates = orderUpdates;
        this.promotions   = promotions;
        this.priceDrops   = priceDrops;
        this.backInStock  = backInStock;
        this.newsletter   = newsletter;
        this.createdAt    = createdAt;
        this.updatedAt    = updatedAt;
    }

    /** Factory: cria com os defaults do schema. */
    public static NotificationPreference create(final UUID userId) {
        final var now = Instant.now();
        return new NotificationPreference(
                UuidV7.generate(), userId,
                true, true, false,
                true, true, true, true, false,
                now, now
        );
    }

    public static NotificationPreference with(
            final UUID id,
            final UUID userId,
            final boolean emailEnabled,
            final boolean pushEnabled,
            final boolean smsEnabled,
            final boolean orderUpdates,
            final boolean promotions,
            final boolean priceDrops,
            final boolean backInStock,
            final boolean newsletter,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new NotificationPreference(
                id, userId, emailEnabled, pushEnabled, smsEnabled,
                orderUpdates, promotions, priceDrops, backInStock, newsletter,
                createdAt, updatedAt
        );
    }

    public void update(
            final boolean emailEnabled,
            final boolean pushEnabled,
            final boolean smsEnabled,
            final boolean orderUpdates,
            final boolean promotions,
            final boolean priceDrops,
            final boolean backInStock,
            final boolean newsletter
    ) {
        this.emailEnabled = emailEnabled;
        this.pushEnabled  = pushEnabled;
        this.smsEnabled   = smsEnabled;
        this.orderUpdates = orderUpdates;
        this.promotions   = promotions;
        this.priceDrops   = priceDrops;
        this.backInStock  = backInStock;
        this.newsletter   = newsletter;
        this.updatedAt    = Instant.now();
    }

    @Override
    public void validate(final ValidationHandler handler) {
        // Todos os campos são booleanos com defaults — sem invariantes a verificar
    }

    public UUID getUserId()          { return userId; }
    public boolean isEmailEnabled()  { return emailEnabled; }
    public boolean isPushEnabled()   { return pushEnabled; }
    public boolean isSmsEnabled()    { return smsEnabled; }
    public boolean isOrderUpdates()  { return orderUpdates; }
    public boolean isPromotions()    { return promotions; }
    public boolean isPriceDrops()    { return priceDrops; }
    public boolean isBackInStock()   { return backInStock; }
    public boolean isNewsletter()    { return newsletter; }
    public Instant getCreatedAt()    { return createdAt; }
    public Instant getUpdatedAt()    { return updatedAt; }
}
