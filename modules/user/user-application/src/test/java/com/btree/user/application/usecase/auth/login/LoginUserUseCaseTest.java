package com.btree.user.application.usecase.auth.login;

import com.btree.shared.contract.PasswordHasher;
import com.btree.shared.contract.TokenHasher;
import com.btree.shared.contract.TokenProvider;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.enums.TokenType;
import com.btree.shared.event.DomainEventPublisher;
import com.btree.shared.exception.DomainException;
import com.btree.shared.validation.Error;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.aggregate_root.Session;
import com.btree.user.domain.aggregate_root.User;
import com.btree.user.domain.entity.LoginHistory;
import com.btree.user.domain.entity.UserToken;
import com.btree.user.domain.error.UserError;
import com.btree.user.domain.persistence.LoginHistoryGateway;
import com.btree.user.domain.persistence.SessionGateway;
import com.btree.user.domain.persistence.UserGateway;
import com.btree.user.domain.persistence.UserTokenGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginUserUseCaseTest {

    private UserGateway userGateway;
    private SessionGateway sessionGateway;
    private UserTokenGateway userTokenGateway;
    private LoginHistoryGateway loginHistoryGateway;
    private PasswordHasher passwordHasher;
    private TokenProvider tokenProvider;
    private TokenHasher tokenHasher;
    private TransactionManager transactionManager;
    private DomainEventPublisher eventPublisher;
    private LoginUserUseCase useCase;

    @BeforeEach
    void SETUP() {
        userGateway = mock(UserGateway.class);
        sessionGateway = mock(SessionGateway.class);
        userTokenGateway = mock(UserTokenGateway.class);
        loginHistoryGateway = mock(LoginHistoryGateway.class);
        passwordHasher = mock(PasswordHasher.class);
        tokenProvider = mock(TokenProvider.class);
        tokenHasher = mock(TokenHasher.class);
        transactionManager = mock(TransactionManager.class);
        eventPublisher = mock(DomainEventPublisher.class);

        when(transactionManager.execute(any())).thenAnswer(invocation -> {
            final Supplier<?> action = invocation.getArgument(0);
            return action.get();
        });

        useCase = new LoginUserUseCase(
                userGateway,
                sessionGateway,
                userTokenGateway,
                loginHistoryGateway,
                passwordHasher,
                tokenProvider,
                tokenHasher,
                transactionManager,
                eventPublisher,
                900_000,
                2_592_000_000L);
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_INPUT_FOR_NULO() {
        final var result = useCase.execute(null);

        assertTrue(result.hasError());
        assertEquals("'input' não pode ser nulo", result.notification().firstError().message());
        verify(userGateway, never()).findByEmail(any());
    }

    @Test
    void DEVE_REALIZAR_LOGIN_COM_SUCESSO() {
        final var user = usuarioValido();
        user.addRole("customer");
        when(userGateway.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("Senha@123", "hash-senha")).thenReturn(true);
        when(tokenProvider.generate(
                eq(user.getId().toString()),
                org.mockito.ArgumentMatchers.<Map<String, Object>>any(),
                any(Instant.class))).thenReturn("access-token");
        when(tokenHasher.generate()).thenReturn("refresh-token");
        when(tokenHasher.hash("refresh-token")).thenReturn("refresh-hash");
        when(sessionGateway.create(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loginHistoryGateway.create(any(LoginHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var result = useCase.execute(new LoginUserInput(
                " Cliente@Example.COM ",
                "Senha@123",
                "127.0.0.1",
                "JUnit"));

        assertTrue(result.isSuccess());
        assertEquals("access-token", result.output().accessToken());
        assertEquals("refresh-token", result.output().refreshToken());
        assertFalse(result.output().requiresTwoFactor());
        assertNull(result.output().transactionId());
        assertTrue(result.output().roles().contains("customer"));

        final var sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(sessionGateway).create(sessionCaptor.capture());
        assertEquals("refresh-hash", sessionCaptor.getValue().getRefreshTokenHash());
        assertEquals("127.0.0.1", sessionCaptor.getValue().getDeviceInfo().getIpAddress());

        verify(loginHistoryGateway).create(any(LoginHistory.class));
        verify(userGateway).update(user);
    }

    @Test
    void DEVE_RETORNAR_CONTA_DESATIVADA_QUANDO_USUARIO_NAO_ESTIVER_HABILITADO() {
        final var user = usuarioValido();
        user.disable();
        when(userGateway.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));

        final var result = useCase.execute(new LoginUserInput(
                "cliente@example.com",
                "Senha@123",
                "127.0.0.1",
                "JUnit"));

        assertTrue(result.hasError());
        assertEquals(UserError.ACCOUNT_DISABLED, result.notification().firstError());
        verify(loginHistoryGateway).create(any(LoginHistory.class));
        verify(passwordHasher, never()).matches(any(), any());
    }

    @Test
    void DEVE_RETORNAR_CREDENCIAIS_INVALIDAS_QUANDO_USUARIO_NAO_EXISTIR() {
        when(userGateway.findByEmail("cliente@example.com")).thenReturn(Optional.empty());

        final var result = useCase.execute(new LoginUserInput(
                "cliente@example.com",
                "Senha@123",
                "127.0.0.1",
                "JUnit"));

        assertTrue(result.hasError());
        assertEquals(UserError.INVALID_CREDENTIALS, result.notification().firstError());
        verify(loginHistoryGateway).create(any(LoginHistory.class));
        verify(passwordHasher, never()).matches(any(), any());
    }

    @Test
    void DEVE_INCREMENTAR_FALHA_E_BLOQUEAR_CONTA_QUANDO_SENHA_FOR_INVALIDA_NO_LIMITE() {
        final var user = usuarioValido();
        for (int i = 0; i < LoginUserUseCase.MAX_FAILED_ATTEMPTS - 1; i++) {
            user.incrementAccessFailed();
        }
        when(userGateway.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("errada", "hash-senha")).thenReturn(false);

        final var result = useCase.execute(new LoginUserInput(
                "cliente@example.com",
                "errada",
                "127.0.0.1",
                "JUnit"));

        assertTrue(result.hasError());
        assertEquals(UserError.INVALID_CREDENTIALS, result.notification().firstError());
        assertTrue(user.isAccountLocked());
        assertNotNull(user.getLockExpiresAt());
        verify(userGateway).update(user);
        verify(loginHistoryGateway).create(any(LoginHistory.class));
        verify(eventPublisher).publishAll(any());
    }

    @Test
    void DEVE_RETORNAR_CONTA_BLOQUEADA_QUANDO_BLOQUEIO_AINDA_ESTIVER_ATIVO() {
        final var user = usuarioValido();
        user.lockAccount(Instant.now().plusSeconds(60));
        when(userGateway.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));

        final var result = useCase.execute(new LoginUserInput(
                "cliente@example.com",
                "Senha@123",
                "127.0.0.1",
                "JUnit"));

        assertTrue(result.hasError());
        assertEquals(UserError.ACCOUNT_LOCKED, result.notification().firstError());
        verify(passwordHasher, never()).matches(any(), any());
        verify(loginHistoryGateway).create(any(LoginHistory.class));
    }

    @Test
    void DEVE_DESBLOQUEAR_CONTA_AUTOMATICAMENTE_QUANDO_BLOQUEIO_EXPIRAR() {
        final var user = usuarioValido();
        user.lockAccount(Instant.now().minusSeconds(60));
        user.clearDomainEvents();
        when(userGateway.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("Senha@123", "hash-senha")).thenReturn(true);
        when(tokenProvider.generate(
                eq(user.getId().toString()),
                org.mockito.ArgumentMatchers.<Map<String, Object>>any(),
                any(Instant.class))).thenReturn("access-token");
        when(tokenHasher.generate()).thenReturn("refresh-token");
        when(tokenHasher.hash("refresh-token")).thenReturn("refresh-hash");

        final var result = useCase.execute(new LoginUserInput(
                "cliente@example.com",
                "Senha@123",
                "127.0.0.1",
                "JUnit"));

        assertTrue(result.isSuccess());
        assertFalse(user.isAccountLocked());
        assertNull(user.getLockExpiresAt());
        verify(userGateway, org.mockito.Mockito.atLeastOnce()).update(user);
        verify(sessionGateway).create(any(Session.class));
    }

    @Test
    void DEVE_RETORNAR_TRANSACAO_DE_DOIS_FATORES_QUANDO_2FA_ESTIVER_ATIVO() {
        final var user = usuarioValido();
        user.enableTwoFactor("segredo");
        user.clearDomainEvents();
        when(userGateway.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("Senha@123", "hash-senha")).thenReturn(true);
        when(tokenHasher.hash(any())).thenReturn("hash-2fa");
        when(userTokenGateway.create(any(UserToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var result = useCase.execute(new LoginUserInput(
                "cliente@example.com",
                "Senha@123",
                "127.0.0.1",
                "JUnit"));

        assertTrue(result.isSuccess());
        assertTrue(result.output().requiresTwoFactor());
        assertNotNull(result.output().transactionId());
        assertNull(result.output().accessToken());
        assertNull(result.output().refreshToken());

        final var tokenCaptor = ArgumentCaptor.forClass(UserToken.class);
        verify(userTokenGateway).create(tokenCaptor.capture());
        assertEquals(TokenType.TWO_FACTOR.name(), tokenCaptor.getValue().getTokenType());
        assertEquals("hash-2fa", tokenCaptor.getValue().getTokenHash());
        verify(sessionGateway, never()).create(any(Session.class));
    }

    @Test
    void DEVE_CONVERTER_DOMAIN_EXCEPTION_EM_NOTIFICATION() {
        final var user = usuarioValido();
        when(userGateway.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("Senha@123", "hash-senha")).thenReturn(true);
        when(tokenProvider.generate(
                eq(user.getId().toString()),
                org.mockito.ArgumentMatchers.<Map<String, Object>>any(),
                any(Instant.class))).thenReturn("access-token");
        when(tokenHasher.generate()).thenReturn("refresh-token");
        when(tokenHasher.hash("refresh-token")).thenReturn("refresh-hash");
        when(sessionGateway.create(any(Session.class)))
                .thenThrow(new DomainException(List.of(new Error("falha de dominio"))));

        final var result = useCase.execute(new LoginUserInput(
                "cliente@example.com",
                "Senha@123",
                "127.0.0.1",
                "JUnit"));

        assertTrue(result.hasError());
        assertEquals("falha de dominio", result.notification().firstError().message());
    }

    @Test
    void DEVE_PROPAGAR_SUBCLASSE_DE_DOMAIN_EXCEPTION() {
        final class TestDomainException extends DomainException {
            TestDomainException() {
                super(List.of(new Error("erro especifico")));
            }
        }

        final var user = usuarioValido();
        when(userGateway.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("Senha@123", "hash-senha")).thenReturn(true);
        when(tokenProvider.generate(
                eq(user.getId().toString()),
                org.mockito.ArgumentMatchers.<Map<String, Object>>any(),
                any(Instant.class))).thenReturn("access-token");
        when(tokenHasher.generate()).thenReturn("refresh-token");
        when(tokenHasher.hash("refresh-token")).thenReturn("refresh-hash");
        when(sessionGateway.create(any(Session.class))).thenThrow(new TestDomainException());

        assertThrows(TestDomainException.class, () -> useCase.execute(new LoginUserInput(
                "cliente@example.com",
                "Senha@123",
                "127.0.0.1",
                "JUnit")));
    }

    private static User usuarioValido() {
        return User.create("cliente_teste", "cliente@example.com", "hash-senha", Notification.create());
    }
}
