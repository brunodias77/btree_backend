package com.btree.shared.event;

import java.util.List;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
    void publishAll(List<? extends DomainEvent> events);
}
