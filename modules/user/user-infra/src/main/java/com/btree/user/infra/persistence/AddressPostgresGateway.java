package com.btree.user.infra.persistence;

import com.btree.shared.exception.NotFoundException;
import com.btree.user.domain.entity.Address;
import com.btree.user.domain.persistence.AddressGateway;
import com.btree.user.infra.model.AddressJpaEntity;
import com.btree.user.infra.repository.AddressJpaRepository;
import com.btree.user.infra.repository.UserJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class AddressPostgresGateway implements AddressGateway {

    private final AddressJpaRepository addressJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public AddressPostgresGateway(
            final AddressJpaRepository addressJpaRepository,
            final UserJpaRepository userJpaRepository
    ) {
        this.addressJpaRepository = addressJpaRepository;
        this.userJpaRepository    = userJpaRepository;
    }

    @Override
    public Address save(final Address address) {
        final var userEntity = userJpaRepository.findById(address.getUserId())
                .orElseThrow(() -> NotFoundException.with("User not found: " + address.getUserId()));

        return addressJpaRepository
                .save(AddressJpaEntity.from(address, userEntity))
                .toAggregate();
    }

    @Override
    public Address update(final Address address) {
        final var entity = addressJpaRepository.findById(address.getId())
                .orElseThrow(() -> NotFoundException.with(Address.class, address.getId()));

        entity.updateFrom(address);
        return addressJpaRepository.save(entity).toAggregate();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Address> findById(final UUID id) {
        return addressJpaRepository.findById(id)
                .map(AddressJpaEntity::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Address> findByUserId(final UUID userId) {
        return addressJpaRepository.findAllActiveByUserId(userId)
                .stream()
                .map(AddressJpaEntity::toAggregate)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveByUserId(final UUID userId) {
        return addressJpaRepository.countActiveByUserId(userId);
    }

    @Override
    public void clearDefaultByUserId(final UUID userId) {
        addressJpaRepository.clearDefaultByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveByUserIdExcluding(final UUID userId, final UUID excludeId) {
        return addressJpaRepository.countActiveByUserIdExcluding(
                userId,
                excludeId
        );
    }
}

