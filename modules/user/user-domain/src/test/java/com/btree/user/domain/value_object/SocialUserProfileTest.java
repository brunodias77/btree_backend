package com.btree.user.domain.value_object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SocialUserProfileTest {

    @Test
    void DEVE_CRIAR_PERFIL_SOCIAL() {
        final var profile = new SocialUserProfile(
                "provider-123",
                "cliente@example.com",
                "Cliente",
                "Teste",
                "https://example.com/avatar.png");

        assertEquals("provider-123", profile.providerUserId());
        assertEquals("cliente@example.com", profile.email());
        assertEquals("Cliente", profile.firstName());
    }

    @Test
    void DEVE_REJEITAR_PROVIDER_USER_ID_VAZIO() {
        assertThrows(IllegalArgumentException.class,
                () -> new SocialUserProfile(" ", "cliente@example.com", "Cliente", "Teste", null));
    }

    @Test
    void DEVE_REJEITAR_EMAIL_VAZIO() {
        assertThrows(IllegalArgumentException.class,
                () -> new SocialUserProfile("provider-123", "", "Cliente", "Teste", null));
    }
}
