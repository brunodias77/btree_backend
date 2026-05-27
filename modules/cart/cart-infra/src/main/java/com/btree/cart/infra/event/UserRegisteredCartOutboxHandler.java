package com.btree.cart.infra.event;

import com.btree.cart.application.usecase.create_cart_for_user.CreateCartForUserInput;
import com.btree.cart.application.usecase.create_cart_for_user.CreateCartForUserUseCase;
import com.btree.shared.event.OutboxEventHandler;
import com.btree.shared.gateway.OutboxEventGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserRegisteredCartOutboxHandler implements OutboxEventHandler {

    private static final String MODULE = "users";
    private static final String EVENT_TYPE = "UserRegistered";

    private final CreateCartForUserUseCase createCartForUserUseCase;

    public UserRegisteredCartOutboxHandler(final CreateCartForUserUseCase createCartForUserUseCase) {
        this.createCartForUserUseCase = createCartForUserUseCase;
    }

    @Override
    public boolean supports(final String module, final String eventType) {
        return MODULE.equals(module) && EVENT_TYPE.equals(eventType);
    }

    @Override
    public void handle(final OutboxEventGateway.PendingEvent event) {
        final var userId = UUID.fromString(event.aggregateId());
        final var result = createCartForUserUseCase.execute(new CreateCartForUserInput(userId));

        if (result.isLeft()) {
            throw new IllegalStateException("Could not create cart for registered user " + userId + ": " + result.getLeft().getErrors());
        }
    }
}
