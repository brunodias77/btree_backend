package com.btree.user.infra.persistence;


import com.btree.shared.exception.NotFoundException;
import com.btree.user.domain.entity.NotificationPreference;
import com.btree.user.domain.persistence.NotificationPreferenceGateway;

import com.btree.user.infra.model.NotificationPreferenceJpaEntity;
import com.btree.user.infra.repository.NotificationPreferenceJpaRepository;
import com.btree.user.infra.repository.UserJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NotificationPreferencePostgresGateway implements NotificationPreferenceGateway {

    private final NotificationPreferenceJpaRepository repository;
    private final UserJpaRepository userJpaRepository;

    public NotificationPreferencePostgresGateway(
            final NotificationPreferenceJpaRepository repository,
            final UserJpaRepository userJpaRepository
    ) {
        this.repository = repository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public NotificationPreference create(final NotificationPreference preference) {
        final var userEntity = userJpaRepository.findById(preference.getUserId())
                .orElseThrow(() -> NotFoundException.with("User not found: " + preference.getUserId()));
        return repository
                .save(NotificationPreferenceJpaEntity.from(preference, userEntity))
                .toAggregate();
    }
}
