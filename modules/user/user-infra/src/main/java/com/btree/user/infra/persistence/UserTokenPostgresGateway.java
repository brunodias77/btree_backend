package com.btree.user.infra.persistence;



import com.btree.shared.exception.NotFoundException;
import com.btree.user.domain.entity.UserToken;
import com.btree.user.domain.persistence.UserTokenGateway;
import com.btree.user.infra.model.UserTokenJpaEntity;
import com.btree.user.infra.repository.UserTokenJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class UserTokenPostgresGateway implements UserTokenGateway {

    private final UserTokenJpaRepository userTokenJpaRepository;

    public UserTokenPostgresGateway(final UserTokenJpaRepository userTokenJpaRepository) {
        this.userTokenJpaRepository = userTokenJpaRepository;
    }

    @Override
    public UserToken create(final UserToken userToken) {
        return userTokenJpaRepository
                .save(UserTokenJpaEntity.from(userToken))
                .toAggregate();
    }

    @Override
    public UserToken update(final UserToken userToken) {
        final var entity = userTokenJpaRepository.findById(userToken.getId())
                .orElseThrow(() -> NotFoundException.with(UserToken.class, userToken.getId()));
        entity.updateFrom(userToken);
        return userTokenJpaRepository.save(entity).toAggregate();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserToken> findByTokenHash(final String tokenHash) {
        return userTokenJpaRepository
                .findByTokenHash(tokenHash)
                .map(UserTokenJpaEntity::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserToken> findById(final UUID id) {
        return userTokenJpaRepository
                .findById(id)
                .map(UserTokenJpaEntity::toAggregate);
    }

    @Override
    public boolean markAsUsedIfActive(final UUID tokenId, final Instant usedAt) {
        return userTokenJpaRepository.markAsUsedIfActive(tokenId, usedAt) == 1;
    }

    @Override
    public int deleteExpired(final int batchSize) {
        return userTokenJpaRepository.deleteExpiredBatch(Instant.now(), batchSize);
    }

    @Override
    public void invalidateActiveByUserIdAndType(final UUID userId, final String tokenType) {
        userTokenJpaRepository.markActiveAsUsedByUserIdAndType(
                userId, tokenType, Instant.now()
        );
    }
}
