package com.btree.shared.exception;

import com.btree.shared.validation.Error;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainExceptionTest {

    @Test
    void DEVE_EXPOR_ERROS_E_USAR_PRIMEIRO_COMO_MENSAGEM() {
        final var exception = DomainException.with(List.of(new Error("first"), new Error("second")));

        assertEquals("first", exception.getMessage());
        assertEquals(2, exception.getErrors().size());
    }

    @Test
    void DEVE_RETORNAR_ERROS_IMUTAVEIS() {
        final var exception = DomainException.with(new Error("failure"));

        assertThrows(UnsupportedOperationException.class,
                () -> exception.getErrors().add(new Error("another")));
    }

    @Test
    void DEVE_CRIAR_MENSAGEM_DE_NAO_ENCONTRADO_COM_CLASSE_E_ID() {
        final var id = UUID.fromString("019e64b8-7d02-7def-ab43-d9a65d468fe9");
        final var exception = NotFoundException.with(DomainExceptionTest.class, id);

        assertEquals("DomainExceptionTest com ID '019e64b8-7d02-7def-ab43-d9a65d468fe9' não foi encontrado", exception.getMessage());
    }
}
