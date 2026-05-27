package com.btree.user.application.usecase.auth.register;

import com.btree.shared.contract.EmailService;
import com.btree.shared.contract.PasswordHasher;
import com.btree.shared.contract.TokenHasher;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.enums.TokenType;
import com.btree.shared.event.DomainEvent;
import com.btree.shared.event.DomainEventPublisher;
import com.btree.shared.event.IntegrationEventPublisher;
import com.btree.shared.event.user.UserRegisteredIntegrationEvent;
import com.btree.user.domain.aggregate_root.User;
import com.btree.user.domain.entity.UserToken;
import com.btree.user.domain.error.UserError;
import com.btree.user.domain.persistence.UserGateway;
import com.btree.user.domain.persistence.UserTokenGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterUserUseCaseTest {

    private UserGateway userGateway;
    private UserTokenGateway userTokenGateway;
    private PasswordHasher passwordHasher;
    private TokenHasher tokenHasher;
    private DomainEventPublisher domainEventPublisher;
    private IntegrationEventPublisher integrationEventPublisher;
    private TransactionManager transactionManager;
    private EmailService emailService;
    private RegisterUserUseCase useCase;

    @BeforeEach
    void SETUP() {
        userGateway = mock(UserGateway.class);
        userTokenGateway = mock(UserTokenGateway.class);
        passwordHasher = mock(PasswordHasher.class);
        tokenHasher = mock(TokenHasher.class);
        domainEventPublisher = mock(DomainEventPublisher.class);
        integrationEventPublisher = mock(IntegrationEventPublisher.class);
        transactionManager = mock(TransactionManager.class);
        emailService = mock(EmailService.class);

        when(transactionManager.execute(any())).thenAnswer(invocation -> {
            final Supplier<?> action = invocation.getArgument(0);
            return action.get();
        });
        doAnswer(invocation -> {
            final Runnable action = invocation.getArgument(0);
            action.run();
            return null;
        }).when(transactionManager).afterCommit(any(Runnable.class));

        useCase = new RegisterUserUseCase(
                userGateway,
                userTokenGateway,
                passwordHasher,
                tokenHasher,
                domainEventPublisher,
                integrationEventPublisher,
                transactionManager,
                emailService);
    }

    @Test
    void DEVE_REGISTRAR_USUARIO_COM_SUCESSO() {
        when(userGateway.existsByUsername("Cliente_Teste")).thenReturn(false);
        when(userGateway.existsByEmail("cliente.teste@example.com")).thenReturn(false);
        when(passwordHasher.hash("Senha@123")).thenReturn("hash-senha");
        when(tokenHasher.generate()).thenReturn("token-bruto");
        when(tokenHasher.hash("token-bruto")).thenReturn("hash-token");
        when(userGateway.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var result = useCase.execute(new RegisterUserInput(
                " Cliente_Teste ",
                " Cliente.Teste@Example.COM ",
                "Senha@123"));

        assertTrue(result.isRight());
        assertEquals("cliente_teste", result.get().username());
        assertEquals("cliente.teste@example.com", result.get().email());
        assertFalse(result.get().emailVerified());

        final var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userGateway).save(userCaptor.capture());
        assertEquals("cliente_teste", userCaptor.getValue().getUsername());
        assertEquals("cliente.teste@example.com", userCaptor.getValue().getEmail());

        final var tokenCaptor = ArgumentCaptor.forClass(UserToken.class);
        verify(userTokenGateway).create(tokenCaptor.capture());
        assertEquals(userCaptor.getValue().getId(), tokenCaptor.getValue().getUserId());
        assertEquals(TokenType.EMAIL_VERIFICATION.name(), tokenCaptor.getValue().getTokenType());
        assertEquals("hash-token", tokenCaptor.getValue().getTokenHash());
        assertTrue(Duration.between(Instant.now(), tokenCaptor.getValue().getExpiresAt()).toHours() >= 23);

        verify(userGateway).createRoleIfNotExists("customer", "Default customer role");
        verify(userGateway).assignRole(userCaptor.getValue().getId(), "customer");
        verify(domainEventPublisher).publishAll(anyList());

        final var eventCaptor = ArgumentCaptor.forClass(UserRegisteredIntegrationEvent.class);
        verify(integrationEventPublisher).publish(eventCaptor.capture());
        assertEquals(userCaptor.getValue().getId().toString(), eventCaptor.getValue().getAggregateId());

        verify(emailService).sendEmailVerification("cliente.teste@example.com", "cliente_teste", "token-bruto");
    }

    @Test
    void DEVE_RETORNAR_ERRO_QUANDO_INPUT_FOR_NULO() {
        final var result = useCase.execute(null);

        assertTrue(result.isLeft());
        assertEquals("'input' não pode ser nulo", result.getLeft().firstError().message());
        verify(transactionManager, never()).execute(any());
    }

    @Test
    void DEVE_RETORNAR_ERROS_QUANDO_SENHA_FOR_FRACA() {
        final var result = useCase.execute(new RegisterUserInput(
                "cliente_teste",
                "cliente@example.com",
                "abc"));

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().getErrors().stream()
                .anyMatch(error -> error.message().contains("mínimo 8")));
        assertTrue(result.getLeft().getErrors().stream()
                .anyMatch(error -> error.message().contains("letra maiúscula")));
        verify(transactionManager, never()).execute(any());
    }

    @Test
    void DEVE_RETORNAR_ERROS_QUANDO_USERNAME_E_EMAIL_JA_EXISTIREM() {
        when(userGateway.existsByUsername("cliente_teste")).thenReturn(true);
        when(userGateway.existsByEmail("cliente@example.com")).thenReturn(true);

        final var result = useCase.execute(new RegisterUserInput(
                "cliente_teste",
                "cliente@example.com",
                "Senha@123"));

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().getErrors().contains(UserError.USERNAME_ALREADY_EXISTS));
        assertTrue(result.getLeft().getErrors().contains(UserError.EMAIL_ALREADY_EXISTS));
        verify(transactionManager, never()).execute(any());
    }

    @Test
    void DEVE_RETORNAR_ERROS_DE_DOMINIO_QUANDO_DADOS_FOREM_INVALIDOS() {
        when(passwordHasher.hash("Senha@123")).thenReturn("hash-senha");

        final var result = useCase.execute(new RegisterUserInput(
                "cliente invalido",
                "email-invalido",
                "Senha@123"));

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().getErrors().stream()
                .anyMatch(error -> error.message().contains("'username' de formato inválido")));
        assertTrue(result.getLeft().getErrors().stream()
                .anyMatch(error -> error.message().contains("'email' de formato inválido")));
    }

    @Test
    void DEVE_PUBLICAR_EVENTOS_DE_DOMINIO_ANTES_DE_LIMPAR_AGREGADO() {
        when(passwordHasher.hash("Senha@123")).thenReturn("hash-senha");
        when(tokenHasher.generate()).thenReturn("token-bruto");
        when(tokenHasher.hash("token-bruto")).thenReturn("hash-token");
        when(userGateway.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(new RegisterUserInput("cliente_teste", "cliente@example.com", "Senha@123"));

        verify(domainEventPublisher).publishAll(argThat(events ->
                !events.isEmpty() && events.getFirst() instanceof DomainEvent));
    }
}
