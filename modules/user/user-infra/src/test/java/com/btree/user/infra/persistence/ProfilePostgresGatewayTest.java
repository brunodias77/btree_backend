package com.btree.user.infra.persistence;

import com.btree.shared.exception.NotFoundException;
import com.btree.user.domain.entity.Profile;
import com.btree.user.infra.model.ProfileJpaEntity;
import com.btree.user.infra.model.UserJpaEntity;
import com.btree.user.infra.repository.ProfileJpaRepository;
import com.btree.user.infra.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfilePostgresGatewayTest {

    private ProfileJpaRepository profileJpaRepository;
    private UserJpaRepository userJpaRepository;
    private ProfilePostgresGateway gateway;

    @BeforeEach
    void SETUP() {
        profileJpaRepository = mock(ProfileJpaRepository.class);
        userJpaRepository = mock(UserJpaRepository.class);
        gateway = new ProfilePostgresGateway(profileJpaRepository, userJpaRepository);
    }

    @Test
    void DEVE_CRIAR_PROFILE_QUANDO_USUARIO_EXISTIR() {
        final var userEntity = new UserJpaEntity();
        userEntity.setId(UUID.randomUUID());
        final var profile = Profile.create(userEntity.getId());
        when(userJpaRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(profileJpaRepository.save(any(ProfileJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var result = gateway.create(profile);

        assertEquals(profile.getId(), result.getId());
        assertEquals(userEntity.getId(), result.getUserId());
    }

    @Test
    void DEVE_LANCAR_NOT_FOUND_AO_CRIAR_PROFILE_SEM_USUARIO() {
        final var profile = Profile.create(UUID.randomUUID());
        when(userJpaRepository.findById(profile.getUserId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gateway.create(profile));

        verify(profileJpaRepository, never()).save(any(ProfileJpaEntity.class));
    }

    @Test
    void DEVE_ATUALIZAR_PROFILE_EXISTENTE() {
        final var userEntity = new UserJpaEntity();
        userEntity.setId(UUID.randomUUID());
        final var profile = Profile.create(userEntity.getId());
        final var entity = ProfileJpaEntity.from(profile, userEntity);
        profile.updatePersonalData("Maria", "Silva", "529.982.247-25", null, "F", null, null, true);
        when(profileJpaRepository.findById(profile.getId())).thenReturn(Optional.of(entity));
        when(profileJpaRepository.save(entity)).thenReturn(entity);

        final var result = gateway.update(profile);

        assertEquals("Maria Silva", result.getDisplayName());
        verify(profileJpaRepository).save(entity);
    }

    @Test
    void DEVE_BUSCAR_PROFILE_ATIVO_POR_USUARIO() {
        final var userEntity = new UserJpaEntity();
        userEntity.setId(UUID.randomUUID());
        final var entity = ProfileJpaEntity.from(Profile.create(userEntity.getId()), userEntity);
        when(profileJpaRepository.findActiveByUserId(userEntity.getId())).thenReturn(Optional.of(entity));

        assertTrue(gateway.findByUserId(userEntity.getId()).isPresent());
    }

    @Test
    void DEVE_DELEGAR_VERIFICACAO_DE_CPF_DUPLICADO() {
        final var userId = UUID.randomUUID();
        when(profileJpaRepository.existsByCpfAndUserIdNot("529.982.247-25", userId)).thenReturn(true);

        assertTrue(gateway.existsByCpfAndNotUserId("529.982.247-25", userId));
    }
}
