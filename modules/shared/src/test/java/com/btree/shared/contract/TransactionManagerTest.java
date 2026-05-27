package com.btree.shared.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionManagerTest {

    @Test
    void DEVE_EXECUTAR_AFTER_COMMIT_PADRAO_IMEDIATAMENTE() {
        final var manager = new ImmediateTransactionManager();
        final var executions = new int[] {0};

        manager.afterCommit(() -> executions[0]++);

        assertEquals(1, executions[0]);
    }

    @Test
    void DEVE_RETORNAR_VALOR_DO_SUPPLIER() {
        final var manager = new ImmediateTransactionManager();

        assertEquals("ok", manager.execute(() -> "ok"));
    }

    private static final class ImmediateTransactionManager implements TransactionManager {
        @Override
        public <T> T execute(final java.util.function.Supplier<T> action) {
            return action.get();
        }

        @Override
        public void executeVoid(final Runnable action) {
            action.run();
        }
    }
}
