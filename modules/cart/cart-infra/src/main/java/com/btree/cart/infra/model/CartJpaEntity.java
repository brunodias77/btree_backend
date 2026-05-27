package com.btree.cart.infra.model;

import com.btree.cart.domain.aggregate_root.Cart;
import com.btree.shared.enums.CartStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "carts", schema = "cart")
public class CartJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "coupon_id")
    private UUID couponId;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "shared.cart_status")
    private CartStatus status;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "converted_at")
    private Instant convertedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CartJpaEntity() {
    }

    private CartJpaEntity(
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
            final Instant updatedAt
    ) {
        this.id = id;
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

    public static CartJpaEntity from(final Cart cart) {
        return new CartJpaEntity(
                cart.getId(),
                cart.getUserId(),
                cart.getSessionId(),
                cart.getCouponId(),
                cart.getCouponCode(),
                cart.getDiscountAmount(),
                cart.getStatus(),
                cart.getIpAddress(),
                cart.getUserAgent(),
                cart.getExpiresAt(),
                cart.getConvertedAt(),
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }

    public Cart toAggregate() {
        return Cart.with(
                this.id,
                this.userId,
                this.sessionId,
                this.couponId,
                this.couponCode,
                this.discountAmount,
                this.status,
                this.ipAddress,
                this.userAgent,
                this.expiresAt,
                this.convertedAt,
                this.createdAt,
                this.updatedAt,
                this.version
        );
    }
}
