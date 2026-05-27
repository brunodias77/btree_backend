package com.btree.shared_infrastructure.event;

import com.btree.shared.event.IntegrationEvent;
import com.btree.shared.event.IntegrationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class OutboxIntegrationEventPublisher implements IntegrationEventPublisher {

    private final DomainEventJpaRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxIntegrationEventPublisher(
            final DomainEventJpaRepository repository,
            final ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(final IntegrationEvent event) {
        repository.save(DomainEventEntity.builder()
                .id(UUID.fromString(event.getEventId()))
                .createdAt(event.getOccurredOn())
                .module(event.getSource())
                .aggregateType(event.getAggregateType())
                .aggregateId(UUID.fromString(event.getAggregateId()))
                .eventType(event.getEventType())
                .payload(serializePayload(event))
                .build());
    }

    private String serializePayload(final IntegrationEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize integration event payload: " + event.getEventType(), e);
        }
    }
}
