package com.btree.user.infra.persistence;

import com.btree.shared.exception.NotFoundException;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.aggregate_root.User;
import com.btree.user.infra.model.RoleJpaEntity;
import com.btree.user.infra.model.UserJpaEntity;
import com.btree.user.infra.repository.RoleJpaRepository;
import com.btree.user.infra.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserPostgresGatewayTest {

    private UserJpaRepository userJpaRepository;
    private RoleJpaRepository roleJpaRepository;
    private UserPostgresGateway gateway;

    @BeforeEach
    void SETUP() {
        userJpaRepository = mock(UserJpaRepository.class);
        roleJpaRepository = mock(RoleJpaRepository.class);
        gateway = new UserPostgresGateway(userJpaRepository, roleJpaRepository);
    }

    @Test
    void DEVE_SALVAR_USUARIO() {
        when(userJpaRepository.save(any(UserJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        final var user = usuarioValido();

        final var result = gateway.save(user);

        assertEquals(user.getId(), result.getId());
        assertEquals("cliente_teste", result.getUsername());
        verify(userJpaRepository).save(any(UserJpaEntity.class));
    }

    @Test
    void DEVE_ATUALIZAR_USUARIO_EXISTENTE() {
        final var user = usuarioValido();
        final var entity = UserJpaEntity.from(user);
        user.verifyEmail();
        when(userJpaRepository.findById(user.getId())).thenReturn(Optional.of(entity));
        when(userJpaRepository.save(entity)).thenReturn(entity);

        final var result = gateway.update(user);

        assertTrue(result.isEmailVerified());
        assertTrue(entity.isEmailVerified());
        verify(userJpaRepository).save(entity);
    }

    @Test
    void DEVE_LANCAR_NOT_FOUND_AO_ATUALIZAR_USUARIO_INEXISTENTE() {
        final var user = usuarioValido();
        when(userJpaRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gateway.update(user));

        verify(userJpaRepository, never()).save(any(UserJpaEntity.class));
    }

    @Test
    void DEVE_DELEGAR_CONSULTAS_DE_EXISTENCIA() {
        when(userJpaRepository.existsByUsernameIgnoreCase("cliente")).thenReturn(true);
        when(userJpaRepository.existsByEmailIgnoreCase("cliente@example.com")).thenReturn(false);

        assertTrue(gateway.existsByUsername("cliente"));
        assertFalse(gateway.existsByEmail("cliente@example.com"));
    }

    @Test
    void DEVE_BUSCAR_USUARIO_POR_EMAIL_E_IDENTIFICADOR() {
        final var user = usuarioValido();
        final var entity = UserJpaEntity.from(user);
        when(userJpaRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(entity));
        when(userJpaRepository.findByUsernameOrEmail("cliente_teste")).thenReturn(Optional.of(entity));

        assertTrue(gateway.findByEmail("cliente@example.com").isPresent());
        assertTrue(gateway.findByUsernameOrEmail("cliente_teste").isPresent());
    }

    @Test
    void DEVE_CRIAR_ROLE_QUANDO_NAO_EXISTIR() {
        when(roleJpaRepository.existsByNameIgnoreCase("customer")).thenReturn(false);

        gateway.createRoleIfNotExists("customer", "Cliente");

        final var captor = ArgumentCaptor.forClass(RoleJpaEntity.class);
        verify(roleJpaRepository).save(captor.capture());
        assertEquals("customer", captor.getValue().getName());
    }

    @Test
    void DEVE_NAO_CRIAR_ROLE_QUANDO_JA_EXISTIR() {
        when(roleJpaRepository.existsByNameIgnoreCase("customer")).thenReturn(true);

        gateway.createRoleIfNotExists("customer", "Cliente");

        verify(roleJpaRepository, never()).save(any(RoleJpaEntity.class));
    }

    @Test
    void DEVE_IGNORAR_CONFLITO_AO_CRIAR_ROLE_CONCORRENTE() {
        when(roleJpaRepository.existsByNameIgnoreCase("customer")).thenReturn(false);
        when(roleJpaRepository.save(any(RoleJpaEntity.class))).thenThrow(new DataIntegrityViolationException("duplicado"));

        gateway.createRoleIfNotExists("customer", "Cliente");

        verify(roleJpaRepository).save(any(RoleJpaEntity.class));
    }

    @Test
    void DEVE_ATRIBUIR_ROLE_AO_USUARIO() {
        final var userId = UUID.randomUUID();
        final var userEntity = new UserJpaEntity();
        userEntity.setId(userId);
        final var roleEntity = new RoleJpaEntity();
        roleEntity.setName("customer");
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleJpaRepository.findByNameIgnoreCase("customer")).thenReturn(Optional.of(roleEntity));

        gateway.assignRole(userId, "customer");

        assertTrue(userEntity.getRoles().contains(roleEntity));
        verify(userJpaRepository).save(userEntity);
    }

    @Test
    void DEVE_LANCAR_NOT_FOUND_AO_ATRIBUIR_ROLE_INEXISTENTE() {
        final var userId = UUID.randomUUID();
        final var userEntity = new UserJpaEntity();
        userEntity.setId(userId);
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleJpaRepository.findByNameIgnoreCase("admin")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gateway.assignRole(userId, "admin"));

        verify(userJpaRepository, never()).save(any(UserJpaEntity.class));
    }

    private static User usuarioValido() {
        return User.create("cliente_teste", "cliente@example.com", "hash-senha", Notification.create());
    }
}
