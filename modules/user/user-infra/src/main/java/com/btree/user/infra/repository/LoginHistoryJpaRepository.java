package com.btree.user.infra.repository;

import com.btree.user.infra.model.LoginHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface LoginHistoryJpaRepository
        extends JpaRepository<LoginHistoryJpaEntity, LoginHistoryJpaEntity.LoginHistoryPk> {
}
