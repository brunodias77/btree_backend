package com.btree.user.infra.persistence;

import com.btree.shared.exception.NotFoundException;
import com.btree.user.domain.entity.LoginHistory;
import com.btree.user.domain.entity.NotificationPreference;
import com.btree.user.domain.value_object.DeviceInfo;
import com.btree.user.infra.model.LoginHistoryJpaEntity;
import com.btree.user.infra.model.NotificationPreferenceJpaEntity;
import com.btree.user.infra.model.UserJpaEntity;
import com.btree.user.infra.repository.LoginHistoryJpaRepository;
import com.btree.user.infra.repository.NotificationPreferenceJpaRepository;
import com.btree.user.infra.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuxiliaryPostgresGatewayTest {

    @Test
    void DEVE_CRIAR_HISTORICO_DE_LOGIN() {
        final var repository = mock(LoginHistoryJpaRepository.class);
        final var gateway = new LoginHistoryPostgresGateway(repository);
        final var history = LoginHistory.recordSuccess(
                UUID.randomUUID(),
                "local",
                DeviceInfo.of("127.0.0.1", "JUnit"));
        when(repository.save(any(LoginHistoryJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var result = gateway.create(history);

        assertEquals(history.getId(), result.getId());
        assertEquals("local", result.getLoginProvider());
    }

    @Test
    void DEVE_CRIAR_PREFERENCIA_DE_NOTIFICACAO_QUANDO_USUARIO_EXISTIR() {
        final var repository = mock(NotificationPreferenceJpaRepository.class);
        final var userJpaRepository = mock(UserJpaRepository.class);
        final var gateway = new NotificationPreferencePostgresGateway(repository, userJpaRepository);
        final var userEntity = new UserJpaEntity();
        userEntity.setId(UUID.randomUUID());
        final var preference = NotificationPreference.create(userEntity.getId());
        when(userJpaRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(repository.save(any(NotificationPreferenceJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var result = gateway.create(preference);

        assertEquals(preference.getId(), result.getId());
        assertEquals(userEntity.getId(), result.getUserId());
    }

    @Test
    void DEVE_LANCAR_NOT_FOUND_AO_CRIAR_PREFERENCIA_SEM_USUARIO() {
        final var repository = mock(NotificationPreferenceJpaRepository.class);
        final var userJpaRepository = mock(UserJpaRepository.class);
        final var gateway = new NotificationPreferencePostgresGateway(repository, userJpaRepository);
        final var preference = NotificationPreference.create(UUID.randomUUID());
        when(userJpaRepository.findById(preference.getUserId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gateway.create(preference));

        verify(repository, never()).save(any(NotificationPreferenceJpaEntity.class));
    }
}
