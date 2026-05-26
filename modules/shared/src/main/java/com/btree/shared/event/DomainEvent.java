package com.btree.shared.event;

import com.btree.shared.contract.UuidGenerator;
import com.btree.shared.util.UuidV7;

import java.time.Instant;
import java.util.Objects;

/**
 * Classe base para todos os eventos de domínio.
 *
 * <p>Recebe {@code module}, {@code aggregateType} e {@code aggregateId} no construtor,
 * eliminando métodos abstratos propensos a typos. Apenas {@link #getEventType()} permanece
 * abstrato, pois é específico de cada evento.
 *
 * <p>O campo {@code module} identifica o serviço/módulo de origem e é mapeado para a coluna
 * {@code module VARCHAR(50) NOT NULL} de {@code shared.domain_events}.
 *
 * <p>O gerador de IDs é substituível via {@link #setIdGenerator} — útil em testes
 * para produzir IDs previsíveis sem depender de {@link UuidV7} diretamente.
 */
public abstract class DomainEvent {

    private static UuidGenerator idGenerator = () -> UuidV7.generate().toString();

    private final String eventId;
    private final Instant occurredOn;
    private final String module;
    private final String aggregateType;
    private final String aggregateId;

    protected DomainEvent(final String module, final String aggregateType, final String aggregateId) {
        this.eventId = idGenerator.generate();
        this.occurredOn = Instant.now();
        this.module = Objects.requireNonNull(module, "'module' não pode ser nulo");
        this.aggregateType = Objects.requireNonNull(aggregateType, "'aggregateType' não pode ser nulo");
        this.aggregateId = Objects.requireNonNull(aggregateId, "'aggregateId' não pode ser nulo");
    }

    /** Substitui o gerador de IDs padrão — use em testes para IDs determinísticos. */
    public static void setIdGenerator(final UuidGenerator generator) {
        idGenerator = Objects.requireNonNull(generator);
    }

    public String getEventId() { return eventId; }
    public Instant getOccurredOn() { return occurredOn; }

    /** Nome do módulo/serviço de origem (ex: {@code "catalog"}). Mapeado para {@code module} no outbox. */
    public String getModule() { return module; }
    public String getAggregateType() { return aggregateType; }
    public String getAggregateId() { return aggregateId; }

    /** Identificador único do tipo de evento (ex: {@code "ProductCreated"}). */
    public abstract String getEventType();
}
