package com.btree.cart.infra.persistence;

import com.btree.cart.domain.aggregate_root.Cart;
import com.btree.cart.domain.persistence.CartGateway;
import com.btree.cart.infra.model.CartJpaEntity;
import com.btree.cart.infra.repository.CartJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional
public class CartPostgresGateway implements CartGateway {

    private final CartJpaRepository cartJpaRepository;

    public CartPostgresGateway(final CartJpaRepository cartJpaRepository) {
        this.cartJpaRepository = cartJpaRepository;
    }

    @Override
    public Cart create(final Cart cart) {
        return cartJpaRepository
                .save(CartJpaEntity.from(cart))
                .toAggregate();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsActiveByUserId(final UUID userId) {
        return cartJpaRepository.existsActiveByUserId(userId);
    }
}
