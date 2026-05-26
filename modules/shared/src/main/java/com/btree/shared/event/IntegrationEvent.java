package com.btree.shared.event;

import com.btree.shared.contract.UuidGenerator;
import com.btree.shared.util.UuidV7;

import java.time.Instant;
import java.util.Objects;

public abstract class IntegrationEvent {

    private static UuidGenerator idGenerator = () -> UuidV7.generate().toString();

    private final String eventId;
    private final Instant occurredOn;
    private final String source;
    private final String aggregateType;
    private final String aggregateId;

    protected IntegrationEvent(final String source) {
        this(source, source, UuidV7.generate().toString());
    }

    protected IntegrationEvent(
            final String source,
            final String aggregateType,
            final String aggregateId
    ) {
        this.eventId = idGenerator.generate();
        this.occurredOn = Instant.now();
        this.source = Objects.requireNonNull(source, "'source' cannot be null");
        this.aggregateType = Objects.requireNonNull(aggregateType, "'aggregateType' cannot be null");
        this.aggregateId = Objects.requireNonNull(aggregateId, "'aggregateId' cannot be null");
    }

    public static void setIdGenerator(final UuidGenerator generator) {
        idGenerator = Objects.requireNonNull(generator);
    }

    public String getEventId() { return eventId; }
    public Instant getOccurredOn() { return occurredOn; }
    public String getSource() { return source; }
    public String getAggregateType() { return aggregateType; }
    public String getAggregateId() { return aggregateId; }

    public abstract String getEventType();
}
