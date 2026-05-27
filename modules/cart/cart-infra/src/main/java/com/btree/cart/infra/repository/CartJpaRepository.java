package com.btree.cart.infra.repository;

import com.btree.cart.infra.model.CartJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CartJpaRepository extends JpaRepository<CartJpaEntity, UUID> {

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                  FROM cart.carts
                 WHERE user_id = :userId
                   AND status = 'ACTIVE'
            )
            """, nativeQuery = true)
    boolean existsActiveByUserId(@Param("userId") UUID userId);
}
