package com.identity.mq;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import javax.enterprise.concurrent.ManagedExecutorService;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor
public class KeycloakEventListenerProvider implements EventListenerProvider {
    private final ManagedExecutorService mes;
    private final List<EventType> excludedEvents;
    private final KeycloakSession session;


    @Override
    public void close() {
        log.debug("Cleaning up");
    }

    @Override
    public void onEvent(Event event) {
        if (nonNull(excludedEvents) && !excludedEvents.contains(event.getType())) {
            log.debug("Received event {}, with details: {}", event.getType(), event.getDetails());
            mes.submit(new KeycloakEventListener(event));
            return;
        }
        log.debug("Ignoring event type: {}", event.getType());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (nonNull(excludedEvents) && !excludedEvents.contains(event.getResourceType())) {
            log.info("Ignoring admin event {}, with operation: {}", event.getResourceType(), event.getRepresentation());
            mes.submit(new KeycloakAdminEventListener(event));
            return;
        }
        log.debug("Ignoring event type: {}", event.getResourceType());
    }

}
