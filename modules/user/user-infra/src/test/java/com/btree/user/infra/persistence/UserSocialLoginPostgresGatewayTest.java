package com.btree.user.infra.persistence;

import com.btree.user.domain.entity.UserSocialLogin;
import com.btree.user.infra.model.UserSocialLoginJpaEntity;
import com.btree.user.infra.repository.UserSocialLoginJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserSocialLoginPostgresGatewayTest {

    private UserSocialLoginJpaRepository repository;
    private UserSocialLoginPostgresGateway gateway;

    @BeforeEach
    void SETUP() {
        repository = mock(UserSocialLoginJpaRepository.class);
        gateway = new UserSocialLoginPostgresGateway(repository);
    }

    @Test
    void DEVE_CRIAR_LOGIN_SOCIAL() {
        final var socialLogin = loginSocialValido();
        when(repository.save(any(UserSocialLoginJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var result = gateway.create(socialLogin);

        assertEquals(socialLogin.getId(), result.getId());
        assertEquals("google", result.getProvider());
    }

    @Test
    void DEVE_BUSCAR_LOGIN_SOCIAL_POR_PROVIDER_E_PROVIDER_USER_ID() {
        final var socialLogin = loginSocialValido();
        when(repository.findByProviderAndProviderUserId("google", "provider-123"))
                .thenReturn(Optional.of(UserSocialLoginJpaEntity.from(socialLogin)));

        assertTrue(gateway.findByProviderAndProviderUserId("google", "provider-123").isPresent());
    }

    @Test
    void DEVE_LISTAR_LOGINS_SOCIAIS_DO_USUARIO() {
        final var socialLogin = loginSocialValido();
        when(repository.findByUserId(socialLogin.getUserId()))
                .thenReturn(List.of(UserSocialLoginJpaEntity.from(socialLogin)));

        assertEquals(1, gateway.findByUserId(socialLogin.getUserId()).size());
    }

    @Test
    void DEVE_DELETAR_LOGIN_SOCIAL_POR_PROVIDER_E_PROVIDER_USER_ID() {
        gateway.deleteByProviderAndProviderUserId("google", "provider-123");

        verify(repository).deleteByProviderAndProviderUserId("google", "provider-123");
    }

    private static UserSocialLogin loginSocialValido() {
        return UserSocialLogin.create(UUID.randomUUID(), "google", "provider-123", "Cliente Teste");
    }
}
