package io.github.spring.middleware.catalog.kafka;

import io.github.spring.middleware.kafka.api.interf.KafkaPublisher;
import io.github.spring.middleware.kafka.core.registry.KafkaPublisherRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class CatalogRunner {

    private final List<String> keys = new ArrayList<>();
    private final Random random = new Random();
    private final KafkaPublisherRegistry publisherRegistry;

    private volatile boolean running = true;

    public CatalogRunner(KafkaPublisherRegistry publisherRegistry) {
        this.publisherRegistry = publisherRegistry;
        IntStream.range(0,5).forEach(i -> keys.add(STR."key#\{UUID.randomUUID().toString()}"));
    }


    public void start() {
        while (running) {
            publishRandomEvent();
            try {
                Thread.sleep(2000); // Sleep for 5 seconds before publishing the next event
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running = false;
    }

    public void restart() {
        running = true;
        Executors.newSingleThreadExecutor().execute(() -> start());
    }

    private void publishRandomEvent() {
        CatalogEvent catalogEvent = new CatalogEvent();
        String key = keys.get(random.nextInt(keys.size()));
        catalogEvent.setEventType(CatalogEventType.CREATED);
        catalogEvent.setKey(key);
        catalogEvent.setCatalog(CatalogMother.randomCatalog());
        KafkaPublisher<CatalogEvent, String> publisher = publisherRegistry.getPublisher("catalog");
        publisher.publishWithKey(catalogEvent, key)
                .thenAccept(result -> {
                    log.info(STR."Published event \{result.getEvent().getEventId()} with key: \{result.getKey()}");

                })
                .exceptionally(ex -> {
                    log.info(STR."Failed to publish event: \{ex.getMessage()}");
                    return null;
                });
    }

}
