package com.btree.shared_infrastructure.job;

import com.btree.shared.contract.TransactionManager;
import com.btree.shared.event.OutboxEventHandler;
import com.btree.shared.gateway.OutboxEventGateway;
import com.btree.shared.gateway.ProcessedEventGateway;
import com.btree.shared.job.Job;
import com.btree.shared.job.JobResult;
import com.btree.shared.usecase.UseCaseResponse;
import com.btree.shared.validation.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProcessDomainEventsJob implements Job<ProcessDomainEvents> {
    private static final Logger log = LoggerFactory.getLogger(ProcessDomainEventsJob.class);

    private final OutboxEventGateway outboxEventGateway;
    private final ProcessedEventGateway processedEventGateway;
    private final TransactionManager transactionManager;
    private final List<OutboxEventHandler> handlers;

    public ProcessDomainEventsJob(
            final OutboxEventGateway outboxEventGateway,
            final ProcessedEventGateway processedEventGateway,
            final TransactionManager transactionManager,
            final List<OutboxEventHandler> handlers
    ) {
        this.outboxEventGateway = outboxEventGateway;
        this.processedEventGateway = processedEventGateway;
        this.transactionManager = transactionManager;
        this.handlers = handlers;
    }

    @Override
    public UseCaseResponse<JobResult> execute(final ProcessDomainEvents input) {
        try {
            final var pending = outboxEventGateway.findPending(input.batchSize());

            if (pending.isEmpty()) {
                log.debug("[ProcessDomainEvents] Nenhum evento pendente.");
                return UseCaseResponse.success(JobResult.empty());
            }

            int processed = 0;
            int skipped = 0;
            int failed = 0;

            for (final var event : pending) {
                try {
                    if (processedEventGateway.alreadyProcessed(event.id())) {
                        outboxEventGateway.markAsProcessed(event.id(), event.createdAt());
                        skipped++;
                        log.debug("[ProcessDomainEvents] Evento {} ja processado, ignorado.", event.id());
                        continue;
                    }

                    transactionManager.execute(() -> {
                        findHandler(event).ifPresentOrElse(
                                handler -> handler.handle(event),
                                () -> log.debug(
                                        "[ProcessDomainEvents] Nenhum handler registrado para {}:{}.",
                                        event.module(),
                                        event.eventType()
                                )
                        );
                        processedEventGateway.recordProcessed(event.id(), event.eventType(), event.module());
                        outboxEventGateway.markAsProcessed(event.id(), event.createdAt());
                        return (Void) null;
                    });

                    processed++;
                    log.debug("[ProcessDomainEvents] Evento {} ({}) processado com sucesso.",
                            event.id(), event.eventType());

                } catch (final Exception e) {
                    try {
                        outboxEventGateway.markAsFailed(event.id(), event.createdAt(), e.getMessage());
                    } catch (final Exception ex) {
                        log.error("[ProcessDomainEvents] Falha ao registrar erro para evento {}: {}",
                                event.id(), ex.getMessage());
                    }
                    failed++;
                    log.warn("[ProcessDomainEvents] Falha ao processar evento {} ({}): {}",
                            event.id(), event.eventType(), e.getMessage());
                }
            }

            if (processed > 0 || failed > 0) {
                log.info("[ProcessDomainEvents] Lote concluido - processados: {}, ignorados: {}, falhas: {}.",
                        processed, skipped, failed);
            }

            return UseCaseResponse.success(JobResult.of(processed, skipped, failed));
        } catch (final Throwable throwable) {
            log.error("[ProcessDomainEvents] Falha inesperada no job: {}",
                    throwable.getMessage(), throwable);
            return UseCaseResponse.failure(Notification.create(throwable));
        }
    }

    private Optional<OutboxEventHandler> findHandler(final OutboxEventGateway.PendingEvent event) {
        return handlers.stream()
                .filter(handler -> handler.supports(event.module(), event.eventType()))
                .findFirst();
    }

    @Scheduled(fixedDelayString = "${outbox.polling.interval-ms:5000}")
    public void schedule() {
        execute(ProcessDomainEvents.withDefaultBatch());
    }
}
