package com.btree.user.infra.persistence;



import com.btree.shared.exception.NotFoundException;
import com.btree.user.domain.entity.Profile;
import com.btree.user.domain.persistence.ProfileGateway;
import com.btree.user.infra.model.ProfileJpaEntity;
import com.btree.user.infra.repository.ProfileJpaRepository;
import com.btree.user.infra.repository.UserJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class ProfilePostgresGateway implements ProfileGateway {

    private final ProfileJpaRepository profileJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public ProfilePostgresGateway(
            final ProfileJpaRepository profileJpaRepository,
            final UserJpaRepository userJpaRepository
    ) {
        this.profileJpaRepository = profileJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Profile create(final Profile profile) {
        final var userEntity = userJpaRepository.findById(profile.getUserId())
                .orElseThrow(() -> NotFoundException.with("User not found: " + profile.getUserId()));

        return profileJpaRepository
                .save(ProfileJpaEntity.from(profile, userEntity))
                .toAggregate();
    }

    @Override
    public Profile update(final Profile profile) {
        final var entity = profileJpaRepository.findById(profile.getId())
                .orElseThrow(() -> NotFoundException.with(Profile.class, profile.getId()));

        entity.updateFrom(profile);
        return profileJpaRepository.save(entity).toAggregate();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findByUserId(final UUID userId) {
        return profileJpaRepository
                .findActiveByUserId(userId)
                .map(ProfileJpaEntity::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCpfAndNotUserId(final String cpf, final UUID userId) {
        return profileJpaRepository.existsByCpfAndUserIdNot(cpf, userId);
    }
}
