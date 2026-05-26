package com.btree.shared_infrastructure.job;

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
