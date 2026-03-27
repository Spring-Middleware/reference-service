package io.github.spring.middleware.catalog.domain;

import io.github.spring.middleware.annotation.graphql.GraphQLLink;
import io.github.spring.middleware.annotation.graphql.GraphQLLinkArgument;
import io.github.spring.middleware.annotation.graphql.GraphQLLinkClass;
import io.github.spring.middleware.graphql.arguments.GraphQLLinkArguments;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@GraphQLLinkClass
public class Catalog {

    private UUID id;
    private String name;
    private String description;
    private CatalogStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    @GraphQLLink(schema = "product", type = "Product", query = "productsByIds", arguments = {
            @GraphQLLinkArgument(name = "ids")
    }, collection = true)
    private List<UUID> productIds;

    @GraphQLQuery(name = "products")
    public List<UUID> getProductIds() {
        return productIds;
    }

    @GraphQLLink(schema = "product", type = "Page_Product", query = "products", arguments = {@GraphQLLinkArgument(name = "catalogId"), @GraphQLLinkArgument(name = "q")})
    @GraphQLQuery(name = "productsByNames")
    public GraphQLLinkArguments getProductNames(@GraphQLArgument(name = "name") String name) {
        return new GraphQLLinkArguments(Map.of("catalogId", id, "q", name));
    }


}
