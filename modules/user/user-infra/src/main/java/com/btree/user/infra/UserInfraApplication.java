package com.btree.user.infra;

import com.btree.shared.contract.PasswordHasher;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.event.DomainEventPublisher;
import com.btree.user.application.usecase.auth.register.RegisterUserUseCase;
import com.btree.user.domain.persistence.UserGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserInfraApplication {

    @Bean
    RegisterUserUseCase registerUserUseCase(
            final UserGateway userGateway,
            final PasswordHasher passwordHasher,
            final DomainEventPublisher domainEventPublisher,
            final TransactionManager transactionManager
    ) {
        return new RegisterUserUseCase(
                userGateway,
                passwordHasher,
                domainEventPublisher,
                transactionManager
        );
    }
}
