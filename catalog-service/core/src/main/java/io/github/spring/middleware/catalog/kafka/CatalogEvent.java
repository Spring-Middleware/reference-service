package io.github.spring.middleware.catalog.kafka;

import io.github.spring.middleware.catalog.domain.Catalog;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogEvent {

    private Catalog catalog;
    private String key; // Optional: can be used for partitioning in Kafka
    private CatalogEventType eventType; // e.g., "CREATED", "UPDATED", "DELETED"
    private CatalogRetryPolicy retryPolicy; // Optional: can be used to determine retry behavior

}
