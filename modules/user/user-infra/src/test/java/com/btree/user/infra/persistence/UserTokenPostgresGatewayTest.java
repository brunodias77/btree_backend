package com.btree.user.infra.persistence;

import com.btree.shared.enums.TokenType;
import com.btree.shared.exception.NotFoundException;
import com.btree.user.domain.entity.UserToken;
import com.btree.user.infra.model.UserTokenJpaEntity;
import com.btree.user.infra.repository.UserTokenJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserTokenPostgresGatewayTest {

    private UserTokenJpaRepository repository;
    private UserTokenPostgresGateway gateway;

    @BeforeEach
    void SETUP() {
        repository = mock(UserTokenJpaRepository.class);
        gateway = new UserTokenPostgresGateway(repository);
    }

    @Test
    void DEVE_CRIAR_TOKEN() {
        when(repository.save(any(UserTokenJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        final var token = tokenValido();

        final var result = gateway.create(token);

        assertEquals(token.getId(), result.getId());
        assertEquals(token.getTokenHash(), result.getTokenHash());
        verify(repository).save(any(UserTokenJpaEntity.class));
    }

    @Test
    void DEVE_ATUALIZAR_TOKEN_EXISTENTE() {
        final var token = tokenValido();
        final var entity = UserTokenJpaEntity.from(token);
        token.markAsUsed();
        when(repository.findById(token.getId())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        final var result = gateway.update(token);

        assertTrue(result.isUsed());
        assertEquals(token.getUsedAt(), entity.getUsedAt());
        verify(repository).save(entity);
    }

    @Test
    void DEVE_LANCAR_NOT_FOUND_AO_ATUALIZAR_TOKEN_INEXISTENTE() {
        final var token = tokenValido();
        when(repository.findById(token.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gateway.update(token));

        verify(repository, never()).save(any(UserTokenJpaEntity.class));
    }

    @Test
    void DEVE_BUSCAR_TOKEN_POR_HASH_E_ID() {
        final var token = tokenValido();
        final var entity = UserTokenJpaEntity.from(token);
        when(repository.findByTokenHash("hash-token")).thenReturn(Optional.of(entity));
        when(repository.findById(token.getId())).thenReturn(Optional.of(entity));

        assertTrue(gateway.findByTokenHash("hash-token").isPresent());
        assertTrue(gateway.findById(token.getId()).isPresent());
    }

    @Test
    void DEVE_RETORNAR_TRUE_QUANDO_MARCAR_TOKEN_ATIVO_COMO_USADO() {
        final var tokenId = UUID.randomUUID();
        final var usedAt = Instant.now();
        when(repository.markAsUsedIfActive(tokenId, usedAt)).thenReturn(1);

        assertTrue(gateway.markAsUsedIfActive(tokenId, usedAt));
    }

    @Test
    void DEVE_RETORNAR_FALSE_QUANDO_NAO_MARCAR_TOKEN_ATIVO_COMO_USADO() {
        final var tokenId = UUID.randomUUID();
        final var usedAt = Instant.now();
        when(repository.markAsUsedIfActive(tokenId, usedAt)).thenReturn(0);

        assertFalse(gateway.markAsUsedIfActive(tokenId, usedAt));
    }

    @Test
    void DEVE_DELETAR_TOKENS_EXPIRADOS_COM_TIMESTAMP_ATUAL() {
        when(repository.deleteExpiredBatch(any(Instant.class), eq(50))).thenReturn(3);

        final var deleted = gateway.deleteExpired(50);

        assertEquals(3, deleted);
        verify(repository).deleteExpiredBatch(any(Instant.class), eq(50));
    }

    @Test
    void DEVE_INVALIDAR_TOKENS_ATIVOS_DO_USUARIO_E_TIPO() {
        final var userId = UUID.randomUUID();

        gateway.invalidateActiveByUserIdAndType(userId, TokenType.EMAIL_VERIFICATION.name());

        final var instantCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(repository).markActiveAsUsedByUserIdAndType(
                eq(userId),
                eq(TokenType.EMAIL_VERIFICATION.name()),
                instantCaptor.capture());
        assertTrue(instantCaptor.getValue().isBefore(Instant.now().plusSeconds(1)));
    }

    private static UserToken tokenValido() {
        return UserToken.create(
                UUID.randomUUID(),
                TokenType.EMAIL_VERIFICATION.name(),
                "hash-token",
                Instant.now().plusSeconds(3600));
    }
}
