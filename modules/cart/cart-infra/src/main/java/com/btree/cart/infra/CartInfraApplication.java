package com.btree.cart.infra;

import com.btree.cart.application.usecase.create_cart_for_user.CreateCartForUserUseCase;
import com.btree.cart.domain.persistence.CartGateway;
import com.btree.shared.contract.TransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartInfraApplication {

    @Bean
    CreateCartForUserUseCase createCartForUserUseCase(
            final CartGateway cartGateway,
            final TransactionManager transactionManager
    ) {
        return new CreateCartForUserUseCase(cartGateway, transactionManager);
    }
}
