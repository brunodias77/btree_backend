package com.btree.shared.usecase;

import com.btree.shared.exception.DomainException;
import com.btree.shared.validation.Error;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCaseResponseTest {

    @Test
    void DEVE_EXECUTAR_ACTION_COM_SUCESSO() {
        final var result = UseCaseResponse.execute(() -> "output");

        assertTrue(result.isSuccess());
        assertEquals("output", result.getOutput());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void DEVE_CONVERTER_DOMAIN_EXCEPTION_EM_FAILURE() {
        final var result = UseCaseResponse.execute(() -> {
            throw new DomainException(List.of(new Error("falha de dominio")));
        });

        assertTrue(result.isFailure());
        assertNull(result.getOutput());
        assertEquals("falha de dominio", result.getErrors().getFirst().message());
    }

    @Test
    void DEVE_EXECUTAR_ACTION_VOID_COM_SUCESSO() {
        final var executed = new AtomicBoolean(false);

        final var result = UseCaseResponse.executeVoid(() -> executed.set(true));

        assertTrue(result.isSuccess());
        assertTrue(executed.get());
        assertNull(result.getOutput());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void DEVE_CONVERTER_DOMAIN_EXCEPTION_EM_FAILURE_NO_VOID() {
        final var result = UseCaseResponse.executeVoid(() -> {
            throw new DomainException(List.of(new Error("falha void")));
        });

        assertTrue(result.isFailure());
        assertNull(result.getOutput());
        assertEquals("falha void", result.getErrors().getFirst().message());
    }
}
