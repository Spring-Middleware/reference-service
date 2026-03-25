package io.github.spring.middleware.catalog.kafka;

import io.github.spring.middleware.kafka.api.annotations.MiddlewareKafkaListener;
import io.github.spring.middleware.kafka.api.data.EventEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CatalogListener {

    @MiddlewareKafkaListener("catalog")
    public void handleCatalogEvent1(EventEnvelope<CatalogEvent> envelope) {
        CatalogEvent catalogEvent = envelope.getPayload();
        log.info("Received catalog event with id {} and key {} en listener catalog", envelope.getEventId(), catalogEvent.getKey());
        if (catalogEvent.getRetryPolicy() == CatalogRetryPolicy.RETRYABLE) {
            log.info("Simulating retryable failure for event with id {}", envelope.getEventId());
            throw new RuntimeException("Transient failure");
        } else {
            log.info("Simulating non-retryable failure for event with id {}", envelope.getEventId());
            throw new IllegalArgumentException("Invalid payload");
        }
    }

}
