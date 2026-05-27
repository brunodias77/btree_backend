package com.btree.user.domain.entity;

import com.btree.shared.validation.Notification;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileTest {

    @Test
    void DEVE_CRIAR_PERFIL_COM_PADROES() {
        final var userId = UUID.randomUUID();

        final var profile = Profile.create(userId);

        assertEquals(userId, profile.getUserId());
        assertEquals("pt-BR", profile.getPreferredLanguage());
        assertEquals("BRL", profile.getPreferredCurrency());
        assertFalse(profile.isNewsletterSubscribed());
        assertNull(profile.getDeletedAt());
    }

    @Test
    void DEVE_ATUALIZAR_DADOS_PESSOAIS_E_MONTAR_NOME_DE_EXIBICAO() {
        final var profile = Profile.create(UUID.randomUUID());

        profile.updatePersonalData(
                " Maria ",
                " Silva ",
                "529.982.247-25",
                LocalDate.of(1990, 1, 1),
                "F",
                null,
                null,
                true);

        assertEquals("Maria Silva", profile.getDisplayName());
        assertEquals("pt-BR", profile.getPreferredLanguage());
        assertEquals("BRL", profile.getPreferredCurrency());
        assertTrue(profile.isNewsletterSubscribed());
    }

    @Test
    void DEVE_VALIDAR_DADOS_INVALIDOS_DO_PERFIL() {
        final var profile = Profile.with(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "A".repeat(101),
                null,
                null,
                null,
                null,
                null,
                "111.111.111-11",
                "p",
                "BR",
                false,
                null,
                null,
                null,
                null,
                null);
        final var notification = Notification.create();

        profile.validate(notification);

        assertTrue(notification.hasError());
        assertEquals(4, notification.getErrors().size());
    }

    @Test
    void DEVE_MARCAR_PERFIL_COMO_REMOVIDO() {
        final var profile = Profile.create(UUID.randomUUID());

        profile.softDelete();

        assertNotNull(profile.getDeletedAt());
    }
}
