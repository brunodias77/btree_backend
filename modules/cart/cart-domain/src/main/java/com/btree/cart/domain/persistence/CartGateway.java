package com.btree.cart.domain.persistence;

import com.btree.cart.domain.aggregate_root.Cart;

import java.util.UUID;

public interface CartGateway {

    Cart create(Cart cart);

    boolean existsActiveByUserId(UUID userId);
}
