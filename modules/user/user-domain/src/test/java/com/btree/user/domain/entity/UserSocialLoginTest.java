package com.btree.user.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserSocialLoginTest {

    @Test
    void DEVE_CRIAR_LOGIN_SOCIAL() {
        final var userId = UUID.randomUUID();

        final var socialLogin = UserSocialLogin.create(userId, "google", "provider-123", "Cliente Teste");

        assertEquals(userId, socialLogin.getUserId());
        assertEquals("google", socialLogin.getProvider());
        assertEquals("provider-123", socialLogin.getProviderUserId());
        assertEquals("Cliente Teste", socialLogin.getProviderDisplayName());
        assertNotNull(socialLogin.getCreatedAt());
    }

    @Test
    void DEVE_REJEITAR_CAMPOS_OBRIGATORIOS_NULOS() {
        assertThrows(NullPointerException.class,
                () -> UserSocialLogin.create(null, "google", "provider-123", "Cliente Teste"));
        assertThrows(NullPointerException.class,
                () -> UserSocialLogin.create(UUID.randomUUID(), null, "provider-123", "Cliente Teste"));
        assertThrows(NullPointerException.class,
                () -> UserSocialLogin.create(UUID.randomUUID(), "google", null, "Cliente Teste"));
    }
}
