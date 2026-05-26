package com.btree.shared.event;

import com.btree.shared.gateway.OutboxEventGateway;

public interface OutboxEventHandler {

    boolean supports(String module, String eventType);

    void handle(OutboxEventGateway.PendingEvent event);
}
