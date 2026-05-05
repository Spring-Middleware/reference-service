package io.github.spring.middleware.catalog.chat.source.graphql;

import io.github.spring.middleware.ai.infrastructure.rag.source.custom.AbstarctCustomDocumentSourceProvider;
import io.github.spring.middleware.ai.infrastructure.rag.source.custom.CustomDocumentSourceProviderOptions;
import io.github.spring.middleware.ai.rag.source.DocumentSource;
import io.github.spring.middleware.ai.rag.source.SourceProviderName;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

@SourceProviderName("catalogs-graphql-provider")
@RequiredArgsConstructor
public class CatalogDocumentGraphQLSource extends AbstarctCustomDocumentSourceProvider {

    private final CatalogDocumentGraphQLSourceConfiguration configuration;
    private final WebClient.Builder webClientBuilder;
    private final ResourceLoader resourceLoader;

    @Override
    protected Flux<DocumentSource> loadSource(String sourceName, CustomDocumentSourceProviderOptions options) {
        WebClient webClient = webClientBuilder
                .baseUrl(configuration.getUrl())
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024) // 5 MB
                )
                .build();

        String query = loadQuery(configuration.getQueryPath());

        return fetchAndProcessPages(webClient, query);
    }

    private String loadQuery(String path) {
        try {
            Resource resource = resourceLoader.getResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(STR."Failed to load GraphQL query from path: \{path}", e);
        }
    }

    private Flux<DocumentSource> fetchAndProcessPages(WebClient webClient, String query) {
        return fetchPageWithContext(webClient, query, 0)
                .expand(wrapper -> {
                    Map<String, Object> catalogs = (Map<String, Object>) wrapper.response().get("catalogs");
                    int totalPages = (int) catalogs.get("totalPages");
                    int currentNumber = wrapper.page();

                    if (currentNumber + 1 >= totalPages) {
                        return Mono.empty();
                    }

                    return fetchPageWithContext(webClient, query, currentNumber + 1);
                })
                .map(wrapper -> {
                    Map<String, Object> catalogs = (Map<String, Object>) wrapper.response().get("catalogs");
                    int number = wrapper.page();

                    String pageId = STR."catalogs-page-\{number}";
                    String jsonContent = serializeToJson(catalogs);

                    return new DocumentSource(
                            pageId,
                            STR."Catalogs Page \{number}",
                            new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8)),
                            "json",
                            "application/json",
                            Map.of("source", configuration.getUrl()),
                            Instant.now()
                    );
                });
    }

    private record PageResponseWrapper(Map<String, Object> response, int page) {}

    private Mono<PageResponseWrapper> fetchPageWithContext(WebClient webClient, String query, int page) {
        return fetchPage(webClient, query, page)
                .map(response -> new PageResponseWrapper(response, page));
    }

    private Mono<Map<String, Object>> fetchPage(WebClient webClient, String query, int page) {
        Map<String, Object> variables = Map.of(
                "page", page,
                "size", configuration.getItemsPerPage(),
                "sort", "name,asc",
                "productSort", "name,asc"
        );
        return postQuery(webClient, query, variables);
    }

    private Mono<Map<String, Object>> postQuery(WebClient webClient, String query, Map<String, Object> variables) {
        return webClient.post()
                .bodyValue(Map.of(
                        "query", query,
                        "variables", variables
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (Map<String, Object>) response.get("data"));
    }

    private String serializeToJson(Object obj) {
        // Simple JSON serialization using Jackson if available, or just use a simple approach
        // Since it's a Spring Boot app, Jackson is likely available.
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize to JSON", e);
        }
    }
}
