package com.btree.user.infra.model;

import com.btree.shared.enums.TokenType;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.aggregate_root.Session;
import com.btree.user.domain.aggregate_root.User;
import com.btree.user.domain.entity.Address;
import com.btree.user.domain.entity.LoginHistory;
import com.btree.user.domain.entity.NotificationPreference;
import com.btree.user.domain.entity.Profile;
import com.btree.user.domain.entity.Role;
import com.btree.user.domain.entity.UserSocialLogin;
import com.btree.user.domain.entity.UserToken;
import com.btree.user.domain.value_object.DeviceInfo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpaEntityConversionTest {

    @Test
    void DEVE_CONVERTER_ROLE_PARA_ENTIDADE_JPA() {
        final var role = Role.create("customer", "Cliente");

        final var entity = RoleJpaEntity.from(role);

        assertEquals(role.getId(), entity.getId());
        assertEquals("customer", entity.getName());
        assertEquals("Cliente", entity.getDescription());
        assertEquals(role.getCreatedAt(), entity.getCreatedAt());
    }

    @Test
    void DEVE_CONVERTER_TOKEN_NOS_DOIS_SENTIDOS() {
        final var token = UserToken.create(
                UUID.randomUUID(),
                TokenType.EMAIL_VERIFICATION.name(),
                "hash-token",
                Instant.now().plusSeconds(3600));

        final var entity = UserTokenJpaEntity.from(token);
        final var aggregate = entity.toAggregate();

        assertEquals(token.getId(), entity.getId());
        assertEquals(token.getUserId(), aggregate.getUserId());
        assertEquals(TokenType.EMAIL_VERIFICATION.name(), aggregate.getTokenType());
        assertEquals("hash-token", aggregate.getTokenHash());
        assertFalse(aggregate.isUsed());

        token.markAsUsed();
        entity.updateFrom(token);

        assertEquals(token.getUsedAt(), entity.getUsedAt());
    }

    @Test
    void DEVE_CONVERTER_SESSAO_NOS_DOIS_SENTIDOS() {
        final var session = Session.create(
                UUID.randomUUID(),
                "refresh-hash",
                DeviceInfo.of("127.0.0.1", "JUnit"),
                Instant.now().plusSeconds(3600));

        final var entity = SessionJpaEntity.from(session);
        final var aggregate = entity.toAggregate();

        assertEquals(session.getId(), entity.getId());
        assertEquals(session.getUserId(), aggregate.getUserId());
        assertEquals("refresh-hash", aggregate.getRefreshTokenHash());
        assertEquals("127.0.0.1", aggregate.getDeviceInfo().getIpAddress());
        assertNull(aggregate.getDeviceInfo().getUserAgent());
        assertTrue(aggregate.isCurrent());
    }

    @Test
    void DEVE_CONVERTER_LOGIN_HISTORY_NOS_DOIS_SENTIDOS() {
        final var history = LoginHistory.recordFailure(
                UUID.randomUUID(),
                "local",
                DeviceInfo.of("127.0.0.1", "JUnit"),
                "Credenciais invalidas");

        final var entity = LoginHistoryJpaEntity.from(history);
        final var aggregate = entity.toAggregate();

        assertEquals(history.getId(), entity.getId());
        assertEquals(history.getUserId(), aggregate.getUserId());
        assertEquals("local", aggregate.getLoginProvider());
        assertEquals("127.0.0.1", aggregate.getDeviceInfo().getIpAddress());
        assertFalse(aggregate.isSuccess());
        assertEquals("Credenciais invalidas", aggregate.getFailureReason());
    }

    @Test
    void DEVE_CONVERTER_LOGIN_SOCIAL_NOS_DOIS_SENTIDOS() {
        final var socialLogin = UserSocialLogin.create(
                UUID.randomUUID(),
                "google",
                "provider-123",
                "Cliente Teste");

        final var entity = UserSocialLoginJpaEntity.from(socialLogin);
        final var aggregate = entity.toAggregate();

        assertEquals(socialLogin.getId(), entity.getId());
        assertEquals(socialLogin.getUserId(), aggregate.getUserId());
        assertEquals("google", aggregate.getProvider());
        assertEquals("provider-123", aggregate.getProviderUserId());
        assertEquals("Cliente Teste", aggregate.getProviderDisplayName());
    }

    @Test
    void DEVE_CONVERTER_PROFILE_NOS_DOIS_SENTIDOS() {
        final var userEntity = UserJpaEntity.from(usuarioValido());
        final var profile = Profile.create(userEntity.getId());
        profile.updatePersonalData("Maria", "Silva", "529.982.247-25", null, "F", null, null, true);

        final var entity = ProfileJpaEntity.from(profile, userEntity);
        final var aggregate = entity.toAggregate();

        assertEquals(profile.getId(), entity.getId());
        assertEquals(userEntity.getId(), aggregate.getUserId());
        assertEquals("Maria Silva", aggregate.getDisplayName());
        assertEquals("pt-BR", aggregate.getPreferredLanguage());
        assertEquals("BRL", aggregate.getPreferredCurrency());
        assertTrue(aggregate.isNewsletterSubscribed());
    }

    @Test
    void DEVE_CONVERTER_PREFERENCIAS_NOS_DOIS_SENTIDOS() {
        final var userEntity = UserJpaEntity.from(usuarioValido());
        final var preference = NotificationPreference.create(userEntity.getId());
        preference.update(false, true, true, false, false, true, false, true);

        final var entity = NotificationPreferenceJpaEntity.from(preference, userEntity);
        final var aggregate = entity.toAggregate();

        assertEquals(preference.getId(), entity.getId());
        assertEquals(userEntity.getId(), aggregate.getUserId());
        assertFalse(aggregate.isEmailEnabled());
        assertTrue(aggregate.isPushEnabled());
        assertTrue(aggregate.isSmsEnabled());
        assertFalse(aggregate.isOrderUpdates());
        assertTrue(aggregate.isNewsletter());
    }

    @Test
    void DEVE_CONVERTER_ADDRESS_NOS_DOIS_SENTIDOS() {
        final var userEntity = UserJpaEntity.from(usuarioValido());
        final var address = Address.create(
                userEntity.getId(),
                "Casa",
                "Cliente Teste",
                "Rua das Flores",
                "123",
                null,
                "Centro",
                "Sao Paulo",
                "SP",
                "01001-000",
                "BR",
                true,
                true,
                Notification.create());

        final var entity = AddressJpaEntity.from(address, userEntity);
        final var aggregate = entity.toAggregate();

        assertEquals(address.getId(), entity.getId());
        assertEquals(userEntity.getId(), aggregate.getUserId());
        assertEquals("Casa", aggregate.getLabel());
        assertEquals("Rua das Flores", aggregate.getStreet());
        assertEquals("Sao Paulo", aggregate.getCity());
        assertTrue(aggregate.isDefault());
        assertTrue(aggregate.isBillingAddress());
    }

    @Test
    void DEVE_RETORNAR_NULL_AO_CONVERTER_RELACIONAMENTOS_NULOS() {
        assertNull(ProfileJpaEntity.from(null, new UserJpaEntity()));
        assertNull(NotificationPreferenceJpaEntity.from(null, new UserJpaEntity()));
    }

    private static User usuarioValido() {
        return User.create("cliente_teste", "cliente@example.com", "hash-senha", Notification.create());
    }
}
