package com.btree.user.application.usecase.auth.verify_email;

import com.btree.shared.contract.TokenHasher;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.enums.TokenType;
import com.btree.shared.event.DomainEventPublisher;
import com.btree.user.domain.aggregate_root.User;
import com.btree.user.domain.entity.UserToken;
import com.btree.user.domain.error.UserError;
import com.btree.user.domain.persistence.UserGateway;
import com.btree.user.domain.persistence.UserTokenGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VerifyEmailUseCaseTest {

    private UserTokenGateway userTokenGateway;
    private UserGateway userGateway;
    private TokenHasher tokenHasher;
    private TransactionManager transactionManager;
    private DomainEventPublisher domainEventPublisher;
    private VerifyEmailUseCase useCase;

    @BeforeEach
    void SETUP() {
        userTokenGateway = mock(UserTokenGateway.class);
        userGateway = mock(UserGateway.class);
        tokenHasher = mock(TokenHasher.class);
        transactionManager = mock(TransactionManager.class);
        domainEventPublisher = mock(DomainEventPublisher.class);

        when(transactionManager.execute(any())).thenAnswer(invocation -> {
            final Supplier<?> action = invocation.getArgument(0);
            return action.get();
        });

        useCase = new VerifyEmailUseCase(
                userTokenGateway,
                userGateway,
                tokenHasher,
                transactionManager,
                domainEventPublisher);
    }

    @Test
    void DEVE_VERIFICAR_EMAIL_COM_SUCESSO() {
        final var user = User.create("cliente_teste", "cliente@example.com", "hash-senha", com.btree.shared.validation.Notification.create());
        user.clearDomainEvents();
        final var userToken = UserToken.create(
                user.getId(),
                TokenType.EMAIL_VERIFICATION.name(),
                "hash-token",
                Instant.now().plusSeconds(3600));
        when(tokenHasher.hash("token-bruto")).thenReturn("hash-token");
        when(userTokenGateway.findByTokenHash("hash-token")).thenReturn(Optional.of(userToken));
        when(userGateway.findById(user.getId())).thenReturn(Optional.of(user));

        final var result = useCase.execute(new VerifyEmailInput(" token-bruto "));

        assertTrue(result.isRight());
        assertTrue(user.isEmailVerified());
        assertTrue(userToken.isUsed());

        final var tokenCaptor = ArgumentCaptor.forClass(UserToken.class);
        verify(userTokenGateway).update(tokenCaptor.capture());
        assertNotNull(tokenCaptor.getValue().getUsedAt());

        verify(userGateway).update(user);
        verify(domainEventPublisher).publishAll(any());
        assertTrue(user.getDomainEvents().isEmpty());
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_INPUT_FOR_NULO() {
        final var result = useCase.execute(null);

        assertTrue(result.isLeft());
        assertEquals("'input' nao pode ser nulo", result.getLeft().firstError().message());
        verify(tokenHasher, never()).hash(any());
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_TOKEN_FOR_VAZIO() {
        final var result = useCase.execute(new VerifyEmailInput(" "));

        assertTrue(result.isLeft());
        assertEquals(UserError.TOKEN_NOT_FOUND, result.getLeft().firstError());
        verify(tokenHasher, never()).hash(any());
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_TOKEN_NAO_FOR_ENCONTRADO() {
        when(tokenHasher.hash("token-bruto")).thenReturn("hash-token");
        when(userTokenGateway.findByTokenHash("hash-token")).thenReturn(Optional.empty());

        final var result = useCase.execute(new VerifyEmailInput("token-bruto"));

        assertTrue(result.isLeft());
        assertEquals(UserError.TOKEN_NOT_FOUND, result.getLeft().firstError());
        verify(transactionManager, never()).execute(any());
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_TOKEN_FOR_DE_OUTRO_TIPO() {
        final var token = UserToken.create(UUID.randomUUID(), "PASSWORD_RESET", "hash-token", Instant.now().plusSeconds(3600));
        when(tokenHasher.hash("token-bruto")).thenReturn("hash-token");
        when(userTokenGateway.findByTokenHash("hash-token")).thenReturn(Optional.of(token));

        final var result = useCase.execute(new VerifyEmailInput("token-bruto"));

        assertTrue(result.isLeft());
        assertEquals(UserError.TOKEN_INVALID_TYPE, result.getLeft().firstError());
        verify(transactionManager, never()).execute(any());
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_TOKEN_ESTIVER_EXPIRADO() {
        final var token = UserToken.with(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TokenType.EMAIL_VERIFICATION.name(),
                "hash-token",
                Instant.now().minusSeconds(1),
                null,
                Instant.now().minusSeconds(3600));
        when(tokenHasher.hash("token-bruto")).thenReturn("hash-token");
        when(userTokenGateway.findByTokenHash("hash-token")).thenReturn(Optional.of(token));

        final var result = useCase.execute(new VerifyEmailInput("token-bruto"));

        assertTrue(result.isLeft());
        assertEquals(UserError.TOKEN_EXPIRED, result.getLeft().firstError());
        verify(transactionManager, never()).execute(any());
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_TOKEN_JA_TIVER_SIDO_USADO() {
        final var token = UserToken.create(
                UUID.randomUUID(),
                TokenType.EMAIL_VERIFICATION.name(),
                "hash-token",
                Instant.now().plusSeconds(3600));
        token.markAsUsed();
        when(tokenHasher.hash("token-bruto")).thenReturn("hash-token");
        when(userTokenGateway.findByTokenHash("hash-token")).thenReturn(Optional.of(token));

        final var result = useCase.execute(new VerifyEmailInput("token-bruto"));

        assertTrue(result.isLeft());
        assertEquals(UserError.TOKEN_ALREADY_USED, result.getLeft().firstError());
        verify(transactionManager, never()).execute(any());
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_USUARIO_NAO_FOR_ENCONTRADO() {
        final var token = UserToken.create(
                UUID.randomUUID(),
                TokenType.EMAIL_VERIFICATION.name(),
                "hash-token",
                Instant.now().plusSeconds(3600));
        when(tokenHasher.hash("token-bruto")).thenReturn("hash-token");
        when(userTokenGateway.findByTokenHash("hash-token")).thenReturn(Optional.of(token));
        when(userGateway.findById(token.getUserId())).thenReturn(Optional.empty());

        final var result = useCase.execute(new VerifyEmailInput("token-bruto"));

        assertTrue(result.isLeft());
        assertEquals(UserError.USER_NOT_FOUND, result.getLeft().firstError());
        verify(transactionManager, never()).execute(any());
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_EMAIL_JA_ESTIVER_VERIFICADO() {
        final var user = User.create("cliente_teste", "cliente@example.com", "hash-senha", com.btree.shared.validation.Notification.create());
        user.verifyEmail();
        final var token = UserToken.create(
                user.getId(),
                TokenType.EMAIL_VERIFICATION.name(),
                "hash-token",
                Instant.now().plusSeconds(3600));
        when(tokenHasher.hash("token-bruto")).thenReturn("hash-token");
        when(userTokenGateway.findByTokenHash("hash-token")).thenReturn(Optional.of(token));
        when(userGateway.findById(user.getId())).thenReturn(Optional.of(user));

        final var result = useCase.execute(new VerifyEmailInput("token-bruto"));

        assertTrue(result.isLeft());
        assertEquals(UserError.EMAIL_ALREADY_VERIFIED, result.getLeft().firstError());
        assertFalse(token.isUsed());
        verify(transactionManager, never()).execute(any());
    }
}
