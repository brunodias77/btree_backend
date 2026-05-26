package com.btree.shared.gateway;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Porta de saída para leitura e atualização dos eventos do outbox
 * ({@code shared.domain_events}).
 *
 * <p>Usada pelos jobs {@code ProcessDomainEventsUseCase} e
 * {@code RetryFailedEventsUseCase} para polling e atualização de status
 * sem acoplamento à camada de persistência.
 */
public interface OutboxEventGateway {

    /**
     * Sumário de um evento pendente, refletindo as colunas de {@code shared.domain_events}
     * necessárias para o job de processamento e roteamento por agregado.
     */
    record PendingEvent(
            UUID id,
            Instant createdAt,
            String module,
            String aggregateType,
            String aggregateId,
            String eventType
    ) {}

    /** Retorna até {@code limit} eventos não processados, ordenados FIFO. */
    List<PendingEvent> findPending(int limit);

    /**
     * Retorna até {@code limit} eventos com falha que ainda podem ser retentados
     * ({@code 0 < retryCount < maxRetries} e {@code processedAt IS NULL}).
     */
    List<PendingEvent> findRetryable(int maxRetries, int limit);

    /** Marca o evento como processado com sucesso (define {@code processedAt}). */
    void markAsProcessed(UUID id, Instant createdAt);

    /** Incrementa {@code retryCount} e registra a mensagem de erro no evento. */
    void markAsFailed(UUID id, Instant createdAt, String error);
}
