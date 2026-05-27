package com.btree.user.infra.persistence;

import com.btree.shared.exception.NotFoundException;
import com.btree.shared.validation.Notification;
import com.btree.user.domain.entity.Address;
import com.btree.user.infra.model.AddressJpaEntity;
import com.btree.user.infra.model.UserJpaEntity;
import com.btree.user.infra.repository.AddressJpaRepository;
import com.btree.user.infra.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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

class AddressPostgresGatewayTest {

    private AddressJpaRepository addressJpaRepository;
    private UserJpaRepository userJpaRepository;
    private AddressPostgresGateway gateway;

    @BeforeEach
    void SETUP() {
        addressJpaRepository = mock(AddressJpaRepository.class);
        userJpaRepository = mock(UserJpaRepository.class);
        gateway = new AddressPostgresGateway(addressJpaRepository, userJpaRepository);
    }

    @Test
    void DEVE_SALVAR_ENDERECO_QUANDO_USUARIO_EXISTIR() {
        final var userEntity = usuarioEntity();
        final var address = enderecoValido(userEntity.getId());
        when(userJpaRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(addressJpaRepository.save(any(AddressJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var result = gateway.save(address);

        assertEquals(address.getId(), result.getId());
        assertEquals(userEntity.getId(), result.getUserId());
        verify(addressJpaRepository).save(any(AddressJpaEntity.class));
    }

    @Test
    void DEVE_LANCAR_NOT_FOUND_AO_SALVAR_ENDERECO_SEM_USUARIO() {
        final var address = enderecoValido(UUID.randomUUID());
        when(userJpaRepository.findById(address.getUserId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gateway.save(address));

        verify(addressJpaRepository, never()).save(any(AddressJpaEntity.class));
    }

    @Test
    void DEVE_ATUALIZAR_ENDERECO_EXISTENTE() {
        final var userEntity = usuarioEntity();
        final var address = enderecoValido(userEntity.getId());
        final var entity = AddressJpaEntity.from(address, userEntity);
        address.updateData("Trabalho", "Cliente", "Avenida Brasil", "100", null, "Centro", "Rio de Janeiro", "RJ", "20000-000", "BR", false);
        when(addressJpaRepository.findById(address.getId())).thenReturn(Optional.of(entity));
        when(addressJpaRepository.save(entity)).thenReturn(entity);

        final var result = gateway.update(address);

        assertEquals("Trabalho", result.getLabel());
        assertEquals("Avenida Brasil", result.getStreet());
        verify(addressJpaRepository).save(entity);
    }

    @Test
    void DEVE_BUSCAR_ENDERECO_POR_ID_E_USUARIO() {
        final var userEntity = usuarioEntity();
        final var entity = AddressJpaEntity.from(enderecoValido(userEntity.getId()), userEntity);
        when(addressJpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(addressJpaRepository.findAllActiveByUserId(userEntity.getId())).thenReturn(List.of(entity));

        assertTrue(gateway.findById(entity.getId()).isPresent());
        assertEquals(1, gateway.findByUserId(userEntity.getId()).size());
    }

    @Test
    void DEVE_DELEGAR_CONTAGENS_E_LIMPEZA_DE_PADRAO() {
        final var userId = UUID.randomUUID();
        final var excludeId = UUID.randomUUID();
        when(addressJpaRepository.countActiveByUserId(userId)).thenReturn(2L);
        when(addressJpaRepository.countActiveByUserIdExcluding(userId, excludeId)).thenReturn(1L);

        assertEquals(2L, gateway.countActiveByUserId(userId));
        assertEquals(1L, gateway.countActiveByUserIdExcluding(userId, excludeId));
        gateway.clearDefaultByUserId(userId);

        verify(addressJpaRepository).clearDefaultByUserId(userId);
    }

    private static UserJpaEntity usuarioEntity() {
        final var user = new UserJpaEntity();
        user.setId(UUID.randomUUID());
        return user;
    }

    private static Address enderecoValido(final UUID userId) {
        return Address.create(
                userId,
                "Casa",
                "Cliente Teste",
                "Rua das Flores",
                "123",
                null,
                "Centro",
                "Sao Paulo",
                "SP",
                "01001-000",
                "BR",
                true,
                true,
                Notification.create());
    }
}
