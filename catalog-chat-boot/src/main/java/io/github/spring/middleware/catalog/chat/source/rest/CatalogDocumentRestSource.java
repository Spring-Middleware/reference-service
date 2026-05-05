package io.github.spring.middleware.catalog.chat.source.rest;

import io.github.spring.middleware.ai.infrastructure.rag.source.custom.AbstarctCustomDocumentSourceProvider;
import io.github.spring.middleware.ai.infrastructure.rag.source.custom.CustomDocumentSourceProviderOptions;
import io.github.spring.middleware.ai.rag.source.DocumentSource;
import io.github.spring.middleware.ai.rag.source.SourceProviderName;
import io.github.spring.middleware.catalog.dto.PagedCatalogResponseDto;
import io.github.spring.middleware.client.proxy.security.oauth2.OAuth2ClientCredentialsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SourceProviderName("catalogs-rest-provider")
@RequiredArgsConstructor
public class CatalogDocumentRestSource extends AbstarctCustomDocumentSourceProvider {

    private final CatalogDocumentRestSourceConfiguration configuration;
    private final OAuth2ClientCredentialsClient clientCredentialsClient;
    private final WebClient.Builder webClientBuilder;

    @Override
    protected Flux<DocumentSource> loadSource(String sourceName, CustomDocumentSourceProviderOptions options) {
        String token = getAccessToken();
        WebClient webClient = webClientBuilder
                .baseUrl(configuration.getUrl())
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024) // 5 MB
                )
                .defaultHeader("Authorization", STR."Bearer \{token}")
                .build();

        return fetchAllCatalogs(webClient)
                .concatMap(catalogId -> fetchCatalogDetails(webClient, catalogId));
    }

    private String getAccessToken() {
        List<String> scopes = configuration.getScopes() != null
                ? Arrays.asList(configuration.getScopes().split(","))
                : List.of();

        return clientCredentialsClient.getAccessToken(
                configuration.getTokenUri(),
                configuration.getClientId(),
                configuration.getClientSecret(),
                scopes
        );
    }

    private Flux<String> fetchAllCatalogs(WebClient webClient) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/catalogs")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .queryParam("sort", "id,desc")
                        .build())
                .retrieve()
                .bodyToMono(PagedCatalogResponseDto.class)
                .expand(response -> {
                    if (response.getPage() + 1 >= response.getTotalPages()) {
                        return Flux.empty();
                    }
                    return webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/api/v1/catalogs")
                                    .queryParam("page", response.getPage() + 1)
                                    .queryParam("size", 10)
                                    .queryParam("sort", "id,desc")
                                    .build())
                            .retrieve()
                            .bodyToMono(PagedCatalogResponseDto.class)
                            .delayElement(java.time.Duration.ofMillis(500)); // Aumentado delay y asegura orden
                })
                .flatMapIterable(PagedCatalogResponseDto::getItems)
                .map(catalog -> catalog.getId().toString());
    }

    private Flux<DocumentSource> fetchCatalogDetails(WebClient webClient, String catalogId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/catalogs/{id}")
                        .queryParam("expand", "products")
                        .build(catalogId))
                .retrieve()
                .bodyToMono(String.class)
                .delayElement(java.time.Duration.ofMillis(200)) // Aumentado delay
                .map(jsonResponse -> new DocumentSource(
                        catalogId,
                        "Catalog " + catalogId,
                        new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8)),
                        "json",
                        "application/json",
                        Map.of("source", configuration.getUrl()),
                        Instant.now()
                ))
                .flux();
    }
}
