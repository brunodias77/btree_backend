package com.btree.cart.application.usecase.create_cart_for_user;

import com.btree.cart.domain.aggregate_root.Cart;
import com.btree.cart.domain.persistence.CartGateway;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.usecase.UnitUseCase;
import com.btree.shared.usecase.UseCaseResponse;
import com.btree.shared.validation.Error;
import com.btree.shared.validation.Notification;

public class CreateCartForUserUseCase implements UnitUseCase<CreateCartForUserInput> {

    private final CartGateway cartGateway;
    private final TransactionManager transactionManager;

    public CreateCartForUserUseCase(
            final CartGateway cartGateway,
            final TransactionManager transactionManager
    ) {
        this.cartGateway = cartGateway;
        this.transactionManager = transactionManager;
    }

    @Override
    public UseCaseResponse<Void> execute(final CreateCartForUserInput input) {
        final var notification = Notification.create();

        if (input == null || input.userId() == null) {
            notification.append(new Error("User id is required to create a cart."));
            return UseCaseResponse.failure(notification);
        }

        if (cartGateway.existsActiveByUserId(input.userId())) {
            return UseCaseResponse.success();
        }

        final var cart = Cart.createForUser(input.userId());
        cart.validate(notification);

        if (notification.hasError()) {
            return UseCaseResponse.failure(notification);
        }

        return UseCaseResponse.from(() -> transactionManager.execute(() -> {
            if (!cartGateway.existsActiveByUserId(input.userId())) {
                cartGateway.create(cart);
            }
            return (Void) null;
        }));
    }
}
