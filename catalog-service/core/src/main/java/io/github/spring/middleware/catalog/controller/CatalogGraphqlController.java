package io.github.spring.middleware.catalog.controller;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogStatus;
import io.github.spring.middleware.catalog.domain.CatalogWithProducts;
import io.github.spring.middleware.catalog.domain.Product;
import io.github.spring.middleware.catalog.dto.*;
import io.github.spring.middleware.catalog.dto.graphql.CatalogDigitalProductUpdateInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogPatchInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogDigitalProductInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogPhysicalProductInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogPhysicalProductUpdateInput;
import io.github.spring.middleware.catalog.mapper.CatalogDtoMapper;
import io.github.spring.middleware.catalog.mapper.CatalogMapper;
import io.github.spring.middleware.catalog.mapper.ProductMapper;
import io.github.spring.middleware.catalog.service.CatalogService;
import io.github.spring.middleware.graphql.annotations.GraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@GraphQLService
public class CatalogGraphqlController {

    private final CatalogService catalogService;
    private final CatalogMapper catalogMapper;
    private final CatalogDtoMapper catalogDtoMapper;
    private final ProductMapper productMapper;

    public CatalogGraphqlController(CatalogService catalogService,
                                    CatalogMapper catalogMapper,
                                    CatalogDtoMapper catalogDtoMapper,
                                    ProductMapper productMapper) {
        this.catalogService = catalogService;
        this.catalogMapper = catalogMapper;
        this.catalogDtoMapper = catalogDtoMapper;
        this.productMapper = productMapper;
    }

    @GraphQLQuery(name = "catalog")
    public Catalog getCatalog(@GraphQLArgument(name = "id") UUID id) {
        CatalogWithProducts catalogWithProducts = catalogService.getCatalog(id, false);
        // Map CatalogWithProducts to basic Catalog view
        Catalog catalog = new Catalog();
        catalog.setId(catalogWithProducts.getId());
        catalog.setName(catalogWithProducts.getName());
        catalog.setStatus(catalogWithProducts.getStatus());
        catalog.setCreatedAt(catalogWithProducts.getCreatedAt());
        catalog.setUpdatedAt(catalogWithProducts.getUpdatedAt());
        if (catalogWithProducts.getProducts() != null) {
            catalog.setProductIds(catalogWithProducts.getProducts().stream()
                    .map(Product::getId)
                    .collect(Collectors.toList()));
        }
        return catalog;
    }

    @GraphQLQuery(name = "catalogWithProducts")
    public CatalogWithProducts getCatalogWithProducts(@GraphQLArgument(name = "id") UUID id) {
        return catalogService.getCatalog(id, true);
    }

    @GraphQLQuery(name = "catalogs")
    public Page<Catalog> listCatalogs(
            @GraphQLArgument(name = "q") String q,
            @GraphQLArgument(name = "status") CatalogStatus status,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size,
            @GraphQLArgument(name = "sort") String sort) {

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                sort != null ? Sort.by(sort.split(",")) : Sort.unsorted()
        );
        return catalogService.listCatalogs(q, status, pageable);
    }

    @GraphQLQuery(name = "catalogProducts")
    public Page<Product> listCatalogProducts(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size) {

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20
        );
        return catalogService.listCatalogProducts(catalogId, pageable);
    }

    @GraphQLMutation(name = "createCatalog")
    public Catalog createCatalog(@GraphQLArgument(name = "input") CatalogInput input) {
        Catalog catalog = catalogMapper.toCatalog(input);
        return catalogService.createCatalog(catalog);
    }

    @GraphQLMutation(name = "replaceCatalog")
    public Catalog replaceCatalog(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") CatalogInput input) {
        Catalog catalog = catalogMapper.toCatalog(input);
        return catalogService.replaceCatalog(id, catalog);
    }

    @GraphQLMutation(name = "patchCatalog")
    public Catalog patchCatalog(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") CatalogPatchInput input) {
        Catalog catalog = catalogMapper.toCatalog(input);
        return catalogService.patchCatalog(id, catalog);
    }

    @GraphQLMutation(name = "deleteCatalog")
    public Boolean deleteCatalog(@GraphQLArgument(name = "id") UUID id) {
        catalogService.deleteCatalog(id);
        return true;
    }

    @GraphQLMutation(name = "addDigitalProductsToCatalog")
    public Catalog addDigitalProductsToCatalog(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "inputs") List<CatalogDigitalProductInput> inputs) {
        var products = inputs.stream()
                .map(catalogMapper::toDigitalProduct)
                .toList();
        return catalogService.addProductsToCatalog(catalogId, products);
    }

    @GraphQLMutation(name = "addPhysicalProductsToCatalog")
    public Catalog addPhysicalProductsToCatalog(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "inputs") List<CatalogPhysicalProductInput> inputs) {
        var products = inputs.stream()
                .map(catalogMapper::toPhysicalProduct)
                .toList();
        return catalogService.addProductsToCatalog(catalogId, products);
    }

    @GraphQLMutation(name = "replaceCatalogDigitalProducts")
    public Catalog replaceCatalogDigitalProducts(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "inputs") List<CatalogDigitalProductUpdateInput> inputs) {
        var products = inputs.stream()
                .map(catalogMapper::toDigitalProduct)
                .toList();
        return catalogService.replaceCatalogProducts(catalogId, products);
    }

    @GraphQLMutation(name = "replaceCatalogPhysicalProducts")
    public Catalog replaceCatalogPhysicalProducts(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "inputs") List<CatalogPhysicalProductUpdateInput> inputs) {
        var products = inputs.stream()
                .map(catalogMapper::toPhysicalProduct)
                .toList();
        return catalogService.replaceCatalogProducts(catalogId, products);
    }

    @GraphQLMutation(name = "removeProductsFromCatalog")
    public Boolean removeProductsFromCatalog(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "productIds") List<UUID> productIds) {
        catalogService.removeProductsFromCatalog(catalogId, productIds);
        return true;
    }
}
