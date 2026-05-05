package io.github.spring.middleware.catalog.chat.source.graphql;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "catalog.document-source.graphql")
public class CatalogDocumentGraphQLSourceConfiguration {

    private String url;
    private String queryPath;
    private Integer itemsPerPage = 10;
}
