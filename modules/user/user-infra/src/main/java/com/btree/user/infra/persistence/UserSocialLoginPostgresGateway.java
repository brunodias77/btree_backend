package com.btree.user.infra.persistence;


import com.btree.user.domain.entity.UserSocialLogin;
import com.btree.user.domain.persistence.UserSocialLoginGateway;
import com.btree.user.infra.model.UserSocialLoginJpaEntity;
import com.btree.user.infra.repository.UserSocialLoginJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class UserSocialLoginPostgresGateway implements UserSocialLoginGateway {

    private final UserSocialLoginJpaRepository repository;

    public UserSocialLoginPostgresGateway(final UserSocialLoginJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserSocialLogin create(final UserSocialLogin userSocialLogin) {
        return repository
                .save(UserSocialLoginJpaEntity.from(userSocialLogin))
                .toAggregate();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSocialLogin> findByProviderAndProviderUserId(
            final String provider,
            final String providerUserId
    ) {
        return repository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .map(UserSocialLoginJpaEntity::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSocialLogin> findByUserId(final UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(UserSocialLoginJpaEntity::toAggregate)
                .toList();
    }

    @Override
    public void deleteByProviderAndProviderUserId(final String provider, final String providerUserId) {
        repository.deleteByProviderAndProviderUserId(provider, providerUserId);
    }
}
