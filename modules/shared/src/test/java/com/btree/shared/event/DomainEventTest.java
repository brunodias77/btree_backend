package com.btree.shared.event;

import com.btree.shared.util.UuidV7;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainEventTest {

    @AfterEach
    void REDEFINIR_GERADOR_DE_ID() {
        DomainEvent.setIdGenerator(() -> UuidV7.generate().toString());
    }

    @Test
    void DEVE_PREENCHER_METADADOS_DO_EVENTO_DE_DOMINIO() {
        DomainEvent.setIdGenerator(() -> "019e64b8-7d02-7def-ab43-d9a65d468fe9");

        final var event = new TestDomainEvent("users", "User", "019e64b8-7d02-7def-ab43-d9a65d468fe9");

        assertEquals("019e64b8-7d02-7def-ab43-d9a65d468fe9", event.getEventId());
        assertEquals("users", event.getModule());
        assertEquals("User", event.getAggregateType());
        assertEquals("019e64b8-7d02-7def-ab43-d9a65d468fe9", event.getAggregateId());
        assertEquals("TestDomainEvent", event.getEventType());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    void DEVE_REJEITAR_METADADOS_NULOS() {
        assertThrows(NullPointerException.class, () -> new TestDomainEvent(null, "User", "1"));
        assertThrows(NullPointerException.class, () -> new TestDomainEvent("users", null, "1"));
        assertThrows(NullPointerException.class, () -> new TestDomainEvent("users", "User", null));
    }

    private static final class TestDomainEvent extends DomainEvent {
        private TestDomainEvent(final String module, final String aggregateType, final String aggregateId) {
            super(module, aggregateType, aggregateId);
        }

        @Override
        public String getEventType() {
            return "TestDomainEvent";
        }
    }
}
