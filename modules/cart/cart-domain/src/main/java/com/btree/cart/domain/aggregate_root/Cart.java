package com.btree.cart.domain.aggregate_root;

import com.btree.shared.abstraction.AggregateRoot;
import com.btree.shared.enums.CartStatus;
import com.btree.shared.util.UuidV7;
import com.btree.shared.validation.Error;
import com.btree.shared.validation.ValidationHandler;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Cart extends AggregateRoot {

    private UUID userId;
    private String sessionId;
    private UUID couponId;
    private String couponCode;
    private BigDecimal discountAmount;
    private CartStatus status;
    private String ipAddress;
    private String userAgent;
    private Instant expiresAt;
    private Instant convertedAt;
    private Instant createdAt;
    private Instant updatedAt;

    private Cart(
            final UUID id,
            final UUID userId,
            final String sessionId,
            final UUID couponId,
            final String couponCode,
            final BigDecimal discountAmount,
            final CartStatus status,
            final String ipAddress,
            final String userAgent,
            final Instant expiresAt,
            final Instant convertedAt,
            final Instant createdAt,
            final Instant updatedAt,
            final int version
    ) {
        super(id, version);
        this.userId = userId;
        this.sessionId = sessionId;
        this.couponId = couponId;
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.status = status;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
        this.convertedAt = convertedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Cart createForUser(final UUID userId) {
        final var now = Instant.now();
        return new Cart(
                UuidV7.generate(),
                userId,
                null,
                null,
                null,
                BigDecimal.ZERO,
                CartStatus.ACTIVE,
                null,
                null,
                null,
                null,
                now,
                now,
                1
        );
    }

    public static Cart with(
            final UUID id,
            final UUID userId,
            final String sessionId,
            final UUID couponId,
            final String couponCode,
            final BigDecimal discountAmount,
            final CartStatus status,
            final String ipAddress,
            final String userAgent,
            final Instant expiresAt,
            final Instant convertedAt,
            final Instant createdAt,
            final Instant updatedAt,
            final int version
    ) {
        return new Cart(
                id,
                userId,
                sessionId,
                couponId,
                couponCode,
                discountAmount,
                status,
                ipAddress,
                userAgent,
                expiresAt,
                convertedAt,
                createdAt,
                updatedAt,
                version
        );
    }

    @Override
    public void validate(final ValidationHandler handler) {
        if (this.userId == null && (this.sessionId == null || this.sessionId.isBlank())) {
            handler.append(new Error("Cart must have an owner user or session."));
        }

        if (this.status == null) {
            handler.append(new Error("Cart status must not be null."));
        }

        if (this.discountAmount != null && this.discountAmount.signum() < 0) {
            handler.append(new Error("Cart discount amount must be greater than or equal to zero."));
        }
    }

    public UUID getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public UUID getCouponId() {
        return couponId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public CartStatus getStatus() {
        return status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getConvertedAt() {
        return convertedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
