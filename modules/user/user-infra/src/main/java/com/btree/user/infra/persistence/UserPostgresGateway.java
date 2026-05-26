package com.btree.user.infra.persistence;


import com.btree.shared.exception.NotFoundException;
import com.btree.user.domain.aggregate_root.User;
import com.btree.user.domain.entity.Role;
import com.btree.user.domain.persistence.UserGateway;
import com.btree.user.infra.model.RoleJpaEntity;
import com.btree.user.infra.model.UserJpaEntity;
import com.btree.user.infra.repository.RoleJpaRepository;
import com.btree.user.infra.repository.UserJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class UserPostgresGateway implements UserGateway {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    public UserPostgresGateway(
            final UserJpaRepository userJpaRepository,
            final RoleJpaRepository roleJpaRepository
    ) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
    }

    // ── Escritas (Write Operations) ───────────────────────────────────────────

    @Override
    public User save(User user) {
        return userJpaRepository
                .save(UserJpaEntity.from(user))
                .toAggregate();
    }

    @Override
    public User update(User user) {
        final var entity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> NotFoundException.with(User.class, user.getId()));
        entity.updateFrom(user);
        return userJpaRepository.save(entity).toAggregate();
    }

    // ── Buscas Booleanas (Checks) ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(final String username) {
        return userJpaRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(final String email) {
        return userJpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public void createRoleIfNotExists(final String roleName, final String description) {
        if (roleJpaRepository.existsByNameIgnoreCase(roleName)) {
            return;
        }

        try {
            roleJpaRepository.save(RoleJpaEntity.from(Role.create(roleName, description)));
        } catch (final DataIntegrityViolationException ignored) {
            // Another transaction may have created the role between the check and the insert.
        }
    }

    // ── Buscas por Entidade (Queries) ─────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(final String email) {
        return userJpaRepository
                .findByEmail(email)
                .map(UserJpaEntity::toAggregate);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(final UUID id) {
        return userJpaRepository
                .findById(id)
                .map(UserJpaEntity::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(final String identifier) {
        return userJpaRepository
                .findByUsernameOrEmail(identifier)
                .map(UserJpaEntity::toAggregate);
    }

    // ── Relações (Relationships) ──────────────────────────────────────────────

    /**
     * Atribui uma role ao usuário.
     *
     * <p>Busca a {@link com.btree.infrastructure.user.entity.RoleJpaEntity} pelo nome
     * e adiciona à coleção do usuário. A relação é persistida via cascade da tabela
     * {@code users.user_roles}.
     *
     * @throws NotFoundException se o usuário ou a role não forem encontrados.
     */
    @Override
    public void assignRole(UUID userId, String roleName) {
        final var userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.with(User.class, userId));

        final var roleEntity = roleJpaRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> NotFoundException.with("Role '%s' não encontrada".formatted(roleName)));

        userEntity.getRoles().add(roleEntity);
        userJpaRepository.save(userEntity);
    }


}
