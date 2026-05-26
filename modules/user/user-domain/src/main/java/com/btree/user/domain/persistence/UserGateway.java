package com.btree.user.domain.persistence;

import com.btree.user.domain.aggregate_root.User;

import java.util.UUID;

public interface UserGateway {
    User save(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void createRoleIfNotExists(String roleName, String description);
    void assignRole(UUID userId, String roleName);
    java.util.Optional<User> findByEmail(String email);
    java.util.Optional<User> findById(UUID id);
    java.util.Optional<User> findByUsernameOrEmail(String identifier);
    User update(User user);
}
