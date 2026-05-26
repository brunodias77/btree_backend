package com.btree.user.domain.persistence;

import com.btree.user.domain.entity.Profile;

import java.util.Optional;
import java.util.UUID;

public interface ProfileGateway {

    Profile create(Profile profile);

    Profile update(Profile profile);

    Optional<Profile> findByUserId(UUID userId);

    boolean existsByCpfAndNotUserId(String cpf, UUID userId);
}
