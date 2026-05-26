package com.btree.shared.event;

import com.btree.shared.contract.UuidGenerator;
import com.btree.shared.util.UuidV7;

import java.time.Instant;
import java.util.Objects;

/**
 * Classe base para eventos de integração publicados para outros serviços/contextos.
 *
 * <p>Diferente de {@link DomainEvent} (interno ao agregado), eventos de integração
 * cruzam fronteiras de contexto e incluem o campo {@code source} para identificar
 * o serviço de origem.
 *
 * <p>O gerador de IDs é substituível via {@link #setIdGenerator} — útil em testes
 * para produzir IDs previsíveis sem depender de {@link UuidV7} diretamente.
 */
public abstract class IntegrationEvent {

    private static UuidGenerator idGenerator = () -> UuidV7.generate().toString();

    private final String eventId;
    private final Instant occurredOn;
    private final String source;

    protected IntegrationEvent(final String source) {
        this.eventId = idGenerator.generate();
        this.occurredOn = Instant.now();
        this.source = Objects.requireNonNull(source, "'source' não pode ser nulo");
    }

    /** Substitui o gerador de IDs padrão — use em testes para IDs determinísticos. */
    public static void setIdGenerator(final UuidGenerator generator) {
        idGenerator = Objects.requireNonNull(generator);
    }

    public String getEventId() { return eventId; }
    public Instant getOccurredOn() { return occurredOn; }

    /** Nome do serviço/módulo que originou o evento (ex: {@code "catalog"}). */
    public String getSource() { return source; }

    /** Identificador único do tipo de evento (ex: {@code "ProductPublished"}). */
    public abstract String getEventType();
}
