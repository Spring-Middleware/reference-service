package io.github.spring.middleware.product.controller;


import io.github.spring.middleware.graphql.annotations.GraphQLService;
import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.domain.ProductStatus;
import io.github.spring.middleware.product.dto.graphql.DigitalProductInput;
import io.github.spring.middleware.product.dto.graphql.PhysicalProductInput;
import io.github.spring.middleware.product.dto.graphql.ProductInput;
import io.github.spring.middleware.product.mapper.ProductMapper;
import io.github.spring.middleware.product.service.ProductService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@GraphQLService
public class ProductGraphqlController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductGraphqlController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GraphQLQuery(name = "product")
    public Product getProduct(@GraphQLArgument(name = "id") UUID id) {
        return productService.getProduct(id);
    }

    @GraphQLQuery(name = "products")
    public Page<Product> listProducts(
            @GraphQLArgument(name = "q") String q,
            @GraphQLArgument(name = "status") ProductStatus status,
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size,
            @GraphQLArgument(name = "sort") String sort) {

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                sort != null ? Sort.by(sort.split(",")) : Sort.unsorted()
        );
        return productService.listProducts(q, status, catalogId, pageable);
    }

    @GraphQLQuery(name = "productsByIds")
    public List<Product> getProductsByIds(@GraphQLArgument(name = "ids") List<UUID> ids) {
        return productService.getProductsByIds(ids);
    }

    @GraphQLMutation(name = "replaceDigitalProduct")
    public Product replaceDigitalProduct(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") DigitalProductInput input) {
        Product product = productMapper.mapToProduct(input);
        return productService.replaceProduct(id, product);
    }

    @GraphQLMutation(name = "replacePhysicalProduct")
    public Product replacePhysicalProduct(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") ProductInput input) {
        Product product = productMapper.mapToProduct(input);
        return productService.replaceProduct(id, product);
    }

    @GraphQLMutation(name = "patchDigitalProduct")
    public Product patchDigitalProduct(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") DigitalProductInput input) {
        Product product = productMapper.mapToProduct(input);
        return productService.patchProduct(id, product);
    }

    @GraphQLMutation(name = "patchPhysicalProduct")
    public Product patchPhysicalProduct(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") ProductInput input) {
        Product product = productMapper.mapToProduct(input);
        return productService.patchProduct(id, product);
    }

    @GraphQLMutation(name = "createDigitalProductsForCatalog")
    public List<Product> createDigitalProductsForCatalog(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "inputs") List<DigitalProductInput> inputs) {
        return createProducts(inputs, catalogId);
    }

    @GraphQLMutation(name = "createPhysicalProductsForCatalog")
    public List<Product> createPhysicalProductsForCatalog(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "inputs") List<PhysicalProductInput> inputs) {
        return createProducts(inputs, catalogId);
    }

    private List<Product> createProducts(List<? extends ProductInput> inputs, UUID catalogId) {
        List<Product> products = inputs.stream()
                .map(productMapper::mapToProduct)
                .collect(Collectors.toList());
        return productService.createProductsForCatalog(products, catalogId);
    }

}

