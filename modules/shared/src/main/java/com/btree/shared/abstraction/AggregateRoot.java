package com.btree.shared.abstraction;
import com.btree.shared.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Raiz de Agregado — ponto de entrada para um cluster de objetos de domínio tratados como unidade.
 *
 * Toda modificação no agregado deve passar pela raiz, garantindo que os invariantes
 * do conjunto sejam sempre preservados. Acrescentaa {@link Entity} duas responsabilidades:
 *
 * <ul>
 *   <li><b>Domain Events</b>: eventos são registrados em memória via {@link #registerEvent}
 *       e despachados após a persistência bem-sucedida (padrão Outbox,
 *       tabela {@code shared.domain_events}).</li>
 *   <li><b>Optimistic Locking</b>: o campo {@code version} é incrementado a cada mudança de
 *       estado e mapeado para a coluna {@code version} da tabela JPA, evitando
 *       escritas concorrentes perdidas (lost update).</li>
 * </ul>
 */
public abstract class AggregateRoot extends Entity {

    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private int version;

    /** Cria o agregado com versão inicial 1 — alinhado com {@code DEFAULT 1} do schema. */
    protected AggregateRoot(final UUID id) {
        this(id, 1);
    }

    /** Reconstitui o agregado a partir da persistência, restaurando a versão salva. */
    protected AggregateRoot(final UUID id, final int version) {
        super(id);
        this.version = version;
    }

    // ── Domain Events ────────────────────────────────────────

    /**
     * Registra um evento de domínio para ser despachado após a persistência do agregado.
     * Ignora silenciosamente valores nulos.
     */
    protected void registerEvent(final DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    /** Retorna uma cópia imutável dos eventos pendentes de despacho. */
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    /**
     * Limpa a lista de eventos após o despacho.
     * Deve ser chamado pelo repositório ou publisher após publicar os eventos com sucesso.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // ── Versioning (Optimistic Locking) ──────────────────────

    /** Retorna a versão atual do agregado, usada pelo JPA para controle de concorrência otimista. */
    public int getVersion() {
        return version;
    }

    /** Incrementa a versão do agregado — deve ser chamado em cada operação que muda o estado. */
    protected void incrementVersion() {
        this.version++;
    }

    /**
     * Aplica uma mudança de estado e incrementa a versão automaticamente.
     *
     * <p>Use sempre que for modificar o estado do agregado para garantir que
     * o optimistic locking seja atualizado. Sem isso, escritas concorrentes
     * podem passar despercebidas pelo JPA.
     *
     * <pre>{@code
     * public void rename(String newName) {
     *     apply(() -> this.name = newName);
     * }
     * }</pre>
     */
    protected final void apply(final Runnable change) {
        change.run();
        incrementVersion();
    }
}

