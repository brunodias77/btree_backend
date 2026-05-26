package com.btree.user.infra.model;

import com.btree.user.domain.entity.LoginHistory;
import com.btree.user.domain.value_object.DeviceInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "login_history", schema = "users")
@IdClass(LoginHistoryJpaEntity.LoginHistoryPk.class)
public class LoginHistoryJpaEntity {

    @Id
    private UUID id;

    @Id
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "login_provider", nullable = false, length = 50)
    private String loginProvider;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "failure_reason", length = 200)
    private String failureReason;

    public LoginHistoryJpaEntity() {}

    private LoginHistoryJpaEntity(
            final UUID id,
            final UUID userId,
            final String loginProvider,
            final String ipAddress,
            final String userAgent,
            final boolean success,
            final String failureReason,
            final Instant createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.loginProvider = loginProvider;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.success = success;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }

    public static LoginHistoryJpaEntity from(final LoginHistory loginHistory) {
        final var deviceInfo = loginHistory.getDeviceInfo();
        return new LoginHistoryJpaEntity(
                loginHistory.getId(),
                loginHistory.getUserId(),
                loginHistory.getLoginProvider(),
                deviceInfo != null ? deviceInfo.getIpAddress() : null,
                deviceInfo != null ? deviceInfo.getUserAgent() : null,
                loginHistory.isSuccess(),
                loginHistory.getFailureReason(),
                loginHistory.getCreatedAt()
        );
    }

    public LoginHistory toAggregate() {
        return LoginHistory.with(
                this.id,
                this.userId,
                this.loginProvider,
                DeviceInfo.of(this.ipAddress, this.userAgent),
                this.success,
                this.failureReason,
                this.createdAt
        );
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getLoginProvider() { return loginProvider; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public boolean isSuccess() { return success; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }

    public static class LoginHistoryPk implements Serializable {
        private UUID id;
        private Instant createdAt;

        public LoginHistoryPk() {}

        public LoginHistoryPk(final UUID id, final Instant createdAt) {
            this.id = id;
            this.createdAt = createdAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LoginHistoryPk that)) return false;
            return Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, createdAt);
        }
    }
}
