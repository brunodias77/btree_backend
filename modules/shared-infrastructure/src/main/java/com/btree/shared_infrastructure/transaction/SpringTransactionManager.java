package com.btree.shared_infrastructure.transaction;

import com.btree.shared.contract.TransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * Adaptador entre o contrato transacional do domínio compartilhado e o Spring.
 *
 * <p>Centraliza o uso de {@link TransactionTemplate} para que os módulos de
 * aplicação não dependam diretamente de APIs do Spring ao executar blocos
 * transacionais ou callbacks após commit.
 */
@Component
public class SpringTransactionManager implements TransactionManager {

    private final TransactionTemplate transactionTemplate;

    public SpringTransactionManager(final TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <T> T execute(final Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }

    @Override
    public void executeVoid(final Runnable action) {
        transactionTemplate.executeWithoutResult(status -> action.run());
    }

    @Override
    public void afterCommit(final Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
