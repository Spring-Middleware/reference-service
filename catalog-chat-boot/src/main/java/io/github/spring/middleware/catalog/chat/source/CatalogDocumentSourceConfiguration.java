package io.github.spring.middleware.catalog.chat.source;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "catalog.document-source")
public class CatalogDocumentSourceConfiguration {

    private String url;
    private String tokenUri;
    private String clientId;
    private String clientSecret;
    private String scopes;
}
