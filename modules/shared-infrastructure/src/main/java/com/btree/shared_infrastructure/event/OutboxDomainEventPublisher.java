package com.btree.shared_infrastructure.event;

import com.btree.shared.event.DomainEvent;
import com.btree.shared.event.DomainEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Component
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private final DomainEventJpaRepository repository;

    private final ObjectMapper objectMapper;

    public OutboxDomainEventPublisher(
            final DomainEventJpaRepository repository,
            final ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }


    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(final DomainEvent event) {
        repository.save(toEntity(event));
    }


    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void publishAll(final List<? extends DomainEvent> events) {
        final var entities = events.stream()
                .map(this::toEntity)
                .toList();
        repository.saveAll(entities);
    }


    private DomainEventEntity toEntity(final DomainEvent event) {
        return DomainEventEntity.builder()
                .id(UUID.fromString(event.getEventId()))
                .createdAt(event.getOccurredOn())
                .module(event.getModule())
                .aggregateType(event.getAggregateType())
                .aggregateId(UUID.fromString(event.getAggregateId()))
                .eventType(event.getEventType())
                .payload(serializePayload(event))
                .build();
    }


    private String serializePayload(final DomainEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize domain event payload: " + event.getEventType(), e);
        }
    }
}
