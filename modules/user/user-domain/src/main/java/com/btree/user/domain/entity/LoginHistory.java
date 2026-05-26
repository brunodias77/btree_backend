package com.btree.user.domain.entity;

import com.btree.shared.abstraction.Entity;
import com.btree.shared.util.UuidV7;
import com.btree.shared.validation.ValidationHandler;
import com.btree.user.domain.value_object.DeviceInfo;

import java.time.Instant;
import java.util.UUID;

public class LoginHistory extends Entity {

    private final UUID userId;
    private final String loginProvider;
    private final DeviceInfo deviceInfo;
    private final boolean success;
    private final String failureReason;
    private final Instant createdAt;

    private LoginHistory(
            final UUID id,
            final UUID userId,
            final String loginProvider,
            final DeviceInfo deviceInfo,
            final boolean success,
            final String failureReason,
            final Instant createdAt
    ) {
        super(id);
        this.userId        = userId;
        this.loginProvider = loginProvider;
        this.deviceInfo    = deviceInfo;
        this.success       = success;
        this.failureReason = failureReason;
        this.createdAt     = createdAt;
    }

    public static LoginHistory recordSuccess(
            final UUID userId,
            final String loginProvider,
            final DeviceInfo deviceInfo
    ) {
        return new LoginHistory(
                UuidV7.generate(), userId, loginProvider,
                deviceInfo, true, null, Instant.now()
        );
    }

    /**
     * Registra uma tentativa de login malsucedida.
     *
     * @param userId        ID do usuário; pode ser {@code null} quando o usuário não existe
     *                      (ex: e-mail desconhecido), pois não há ID para associar ao registro.
     * @param loginProvider provedor de login (ex: {@code "Local"}, {@code "google"})
     * @param deviceInfo    informações do dispositivo da tentativa
     * @param failureReason descrição do motivo da falha
     */
    public static LoginHistory recordFailure(
            final UUID userId,
            final String loginProvider,
            final DeviceInfo deviceInfo,
            final String failureReason
    ) {
        return new LoginHistory(
                UuidV7.generate(), userId, loginProvider,
                deviceInfo, false, failureReason, Instant.now()
        );
    }

    public static LoginHistory with(
            final UUID id,
            final UUID userId,
            final String loginProvider,
            final DeviceInfo deviceInfo,
            final boolean success,
            final String failureReason,
            final Instant createdAt
    ) {
        return new LoginHistory(id, userId, loginProvider, deviceInfo, success, failureReason, createdAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        // Registro imutável — sem invariantes de domínio a verificar
    }

    public UUID getUserId()          { return userId; }
    public String getLoginProvider() { return loginProvider; }
    public DeviceInfo getDeviceInfo(){ return deviceInfo; }
    public boolean isSuccess()       { return success; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt()    { return createdAt; }
}
