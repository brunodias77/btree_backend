package com.btree.shared.validation;

import com.btree.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThrowsValidationHandlerTest {

    @Test
    void DEVE_LANCAR_DOMAIN_EXCEPTION_AO_ADICIONAR_ERRO() {
        final var ex = assertThrows(DomainException.class,
                () -> ThrowsValidationHandler.INSTANCE.append(new Error("failure")));

        assertEquals("failure", ex.getErrors().getFirst().message());
    }

    @Test
    void DEVE_LANCAR_DOMAIN_EXCEPTION_COM_TODOS_OS_ERROS_DO_HANDLER() {
        final var notification = Notification.create(new Error("first"))
                .append(new Error("second"));

        final var ex = assertThrows(DomainException.class,
                () -> ThrowsValidationHandler.INSTANCE.append(notification));

        assertEquals(2, ex.getErrors().size());
    }
}
