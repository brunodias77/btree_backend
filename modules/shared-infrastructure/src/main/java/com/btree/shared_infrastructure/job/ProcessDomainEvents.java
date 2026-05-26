package com.btree.shared_infrastructure.job;

/**
 * Comando de entrada do job de processamento de eventos de domínio.
 *
 * <p>Carrega apenas o tamanho do lote para manter o scheduler simples e permitir
 * que outros chamadores executem o mesmo job com lotes maiores ou menores.
 */
public record ProcessDomainEvents(int batchSize) {

    public ProcessDomainEvents {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("'batchSize' deve ser maior que zero");
        }
    }

    /** Factory com valor padrão seguro para uso no scheduler. */
    public static ProcessDomainEvents withDefaultBatch() {
        return new ProcessDomainEvents(100);
    }
}
