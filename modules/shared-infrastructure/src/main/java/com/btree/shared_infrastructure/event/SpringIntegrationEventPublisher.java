package com.btree.shared_infrastructure.event;

import com.btree.shared.contract.TransactionManager;
import com.btree.shared.event.IntegrationEvent;
import com.btree.shared.event.IntegrationEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringIntegrationEventPublisher implements IntegrationEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TransactionManager transactionManager;

    public SpringIntegrationEventPublisher(
            final ApplicationEventPublisher applicationEventPublisher,
            final TransactionManager transactionManager
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.transactionManager = transactionManager;
    }

    @Override
    public void publish(final IntegrationEvent event) {
        transactionManager.afterCommit(() -> applicationEventPublisher.publishEvent(event));
    }
}
