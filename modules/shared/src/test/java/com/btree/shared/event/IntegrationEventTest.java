package com.btree.shared.event;

import com.btree.shared.event.user.UserRegisteredIntegrationEvent;
import com.btree.shared.util.UuidV7;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntegrationEventTest {

    @AfterEach
    void REDEFINIR_GERADOR_DE_ID() {
        IntegrationEvent.setIdGenerator(() -> UuidV7.generate().toString());
    }

    @Test
    void DEVE_EXPOR_METADADOS_DE_OUTBOX_DO_USUARIO_REGISTRADO() {
        final var userId = UUID.fromString("019e64b8-7d02-7def-ab43-d9a65d468fe9");
        IntegrationEvent.setIdGenerator(() -> "019e64b8-7ebc-76e9-a51f-8c4fc7e4e247");

        final var event = new UserRegisteredIntegrationEvent(userId, "user@example.com");

        assertEquals("019e64b8-7ebc-76e9-a51f-8c4fc7e4e247", event.getEventId());
        assertEquals("users", event.getSource());
        assertEquals("User", event.getAggregateType());
        assertEquals(userId.toString(), event.getAggregateId());
        assertEquals("UserRegistered", event.getEventType());
        assertEquals(userId, event.getUserId());
        assertEquals("user@example.com", event.getEmail());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    void DEVE_REJEITAR_ORIGEM_NULA() {
        assertThrows(NullPointerException.class, () -> new TestIntegrationEvent(null, "User", "1"));
    }

    private static final class TestIntegrationEvent extends IntegrationEvent {
        private TestIntegrationEvent(final String source, final String aggregateType, final String aggregateId) {
            super(source, aggregateType, aggregateId);
        }

        @Override
        public String getEventType() {
            return "TestIntegrationEvent";
        }
    }
}
