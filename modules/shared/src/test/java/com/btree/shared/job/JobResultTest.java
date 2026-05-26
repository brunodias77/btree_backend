package com.btree.shared.job;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobResultTest {

    @Test
    void DEVE_SOMAR_TODOS_OS_CONTADORES() {
        final var result = JobResult.of(2, 3, 1);

        assertEquals(6, result.total());
        assertTrue(result.hasFailures());
        assertEquals("JobResult{processed=2, skipped=3, failed=1, total=6}", result.toString());
    }

    @Test
    void DEVE_CRIAR_RESULTADO_VAZIO_SEM_FALHAS() {
        final var result = JobResult.empty();

        assertEquals(0, result.total());
        assertFalse(result.hasFailures());
    }
}
