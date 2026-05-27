package com.btree.user.domain.aggregate_root;

import com.btree.shared.exception.DomainException;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.event.UserCreatedEvent;
import com.btree.user.domain.event.UserEmailChangedEvent;
import com.btree.user.domain.event.UserEmailVerifiedEvent;
import com.btree.user.domain.event.UserTwoFactorDisabledEvent;
import com.btree.user.domain.event.UserTwoFactorEnabledEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void DEVE_CRIAR_USUARIO_COM_PERFIL_PREFERENCIAS_E_EVENTO() {
        final var user = User.create("cliente_teste", "cliente@example.com", "hash-seguro", Notification.create());

        assertNotNull(user.getId());
        assertEquals("cliente_teste", user.getUsername());
        assertEquals("cliente@example.com", user.getEmail());
        assertFalse(user.isEmailVerified());
        assertTrue(user.isRequiresPassword());
        assertTrue(user.isEnabled());
        assertEquals(1, user.getVersion());
        assertNotNull(user.getProfile());
        assertNotNull(user.getNotificationPreference());
        assertEquals(1, user.getDomainEvents().size());
        assertInstanceOf(UserCreatedEvent.class, user.getDomainEvents().getFirst());
    }

    @Test
    void DEVE_REJEITAR_USUARIO_COM_DADOS_INVALIDOS() {
        final var notification = Notification.create();

        final var exception = assertThrows(DomainException.class,
                () -> User.create("cliente invalido", "email-invalido", "", notification));

        assertTrue(exception.getErrors().stream()
                .anyMatch(error -> error.message().contains("'username' de formato inválido")));
        assertTrue(exception.getErrors().stream()
                .anyMatch(error -> error.message().contains("'email' de formato inválido")));
        assertTrue(exception.getErrors().stream()
                .anyMatch(error -> error.message().contains("'passwordHash' não pode ser nulo ou vazio")));
    }

    @Test
    void DEVE_VERIFICAR_EMAIL_E_REGISTRAR_EVENTO() {
        final var user = usuarioValido();
        user.clearDomainEvents();
        final var version = user.getVersion();

        user.verifyEmail();

        assertTrue(user.isEmailVerified());
        assertEquals(version + 1, user.getVersion());
        assertEquals(1, user.getDomainEvents().size());
        assertInstanceOf(UserEmailVerifiedEvent.class, user.getDomainEvents().getFirst());
    }

    @Test
    void DEVE_ALTERAR_EMAIL_MARCANDO_COMO_NAO_VERIFICADO() {
        final var user = usuarioValido();
        user.verifyEmail();
        user.clearDomainEvents();
        final var version = user.getVersion();

        user.changeEmail("novo@example.com");

        assertEquals("novo@example.com", user.getEmail());
        assertFalse(user.isEmailVerified());
        assertEquals(version + 1, user.getVersion());
        assertInstanceOf(UserEmailChangedEvent.class, user.getDomainEvents().getFirst());
    }

    @Test
    void DEVE_HABILITAR_E_DESABILITAR_DOIS_FATORES() {
        final var user = usuarioValido();
        user.clearDomainEvents();

        user.enableTwoFactor("segredo");

        assertTrue(user.isTwoFactorEnabled());
        assertEquals("segredo", user.getTwoFactorSecret());
        assertInstanceOf(UserTwoFactorEnabledEvent.class, user.getDomainEvents().getFirst());

        user.clearDomainEvents();
        user.disableTwoFactor();

        assertFalse(user.isTwoFactorEnabled());
        assertNull(user.getTwoFactorSecret());
        assertInstanceOf(UserTwoFactorDisabledEvent.class, user.getDomainEvents().getFirst());
    }

    @Test
    void DEVE_BLOQUEAR_E_DESBLOQUEAR_CONTA() {
        final var user = usuarioValido();
        final var expiresAt = Instant.now().minusSeconds(60);

        user.incrementAccessFailed();
        user.lockAccount(expiresAt);

        assertTrue(user.isAccountLocked());
        assertTrue(user.isLockExpired());
        assertEquals(expiresAt, user.getLockExpiresAt());

        user.unlockAccount();

        assertFalse(user.isAccountLocked());
        assertNull(user.getLockExpiresAt());
        assertEquals(0, user.getAccessFailedCount());
    }

    @Test
    void DEVE_ADICIONAR_APENAS_ROLES_VALIDAS() {
        final var user = usuarioValido();

        user.addRole("customer");
        user.addRole(" ");
        user.addRole(null);

        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains("customer"));
        assertThrows(UnsupportedOperationException.class, () -> user.getRoles().add("admin"));
    }

    private static User usuarioValido() {
        return User.create("cliente_teste", "cliente@example.com", "hash-seguro", Notification.create());
    }
}
