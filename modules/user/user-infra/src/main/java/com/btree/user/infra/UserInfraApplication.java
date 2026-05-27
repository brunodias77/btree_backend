package com.btree.user.infra;

import com.btree.shared.contract.EmailService;
import com.btree.shared.contract.PasswordHasher;
import com.btree.shared.contract.TokenHasher;
import com.btree.shared.contract.TokenProvider;
import com.btree.shared.contract.TransactionManager;
import com.btree.shared.event.DomainEventPublisher;
import com.btree.shared.event.IntegrationEventPublisher;
import com.btree.shared_infrastructure.security.JwtProperties;
import com.btree.user.application.usecase.auth.login.LoginUserUseCase;
import com.btree.user.application.usecase.auth.register.RegisterUserUseCase;
import com.btree.user.application.usecase.auth.verify_email.VerifyEmailUseCase;
import com.btree.user.domain.persistence.LoginHistoryGateway;
import com.btree.user.domain.persistence.SessionGateway;
import com.btree.user.domain.persistence.UserGateway;
import com.btree.user.domain.persistence.UserTokenGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class UserInfraApplication {

    @Bean
    RegisterUserUseCase registerUserUseCase(
            final UserGateway userGateway,
            final UserTokenGateway userTokenGateway,
            final PasswordHasher passwordHasher,
            final TokenHasher tokenHasher,
            final DomainEventPublisher domainEventPublisher,
            final IntegrationEventPublisher integrationEventPublisher,
            final TransactionManager transactionManager,
            final EmailService emailService
    ) {
        return new RegisterUserUseCase(
                userGateway,
                userTokenGateway,
                passwordHasher,
                tokenHasher,
                domainEventPublisher,
                integrationEventPublisher,
                transactionManager,
                emailService
        );
    }

    @Bean
    LoginUserUseCase loginUserUseCase(
            final UserGateway userGateway,
            final SessionGateway sessionGateway,
            final UserTokenGateway userTokenGateway,
            final LoginHistoryGateway loginHistoryGateway,
            final PasswordHasher passwordHasher,
            final TokenProvider tokenProvider,
            final TokenHasher tokenHasher,
            final TransactionManager transactionManager,
            final DomainEventPublisher domainEventPublisher,
            final JwtProperties jwtProperties
    ) {
        return new LoginUserUseCase(
                userGateway,
                sessionGateway,
                userTokenGateway,
                loginHistoryGateway,
                passwordHasher,
                tokenProvider,
                tokenHasher,
                transactionManager,
                domainEventPublisher,
                Duration.ofMinutes(jwtProperties.getAccessTokenTtlMinutes()).toMillis(),
                Duration.ofDays(jwtProperties.getRefreshTokenTtlDays()).toMillis()
        );
    }

    @Bean
    VerifyEmailUseCase verifyEmailUseCase(
            final UserTokenGateway userTokenGateway,
            final UserGateway userGateway,
            final TokenHasher tokenHasher,
            final TransactionManager transactionManager,
            final DomainEventPublisher domainEventPublisher
    ) {
        return new VerifyEmailUseCase(
                userTokenGateway,
                userGateway,
                tokenHasher,
                transactionManager,
                domainEventPublisher
        );
    }
}
