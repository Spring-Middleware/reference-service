package io.github.spring.middleware.catalog.controller;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogStatus;
import io.github.spring.middleware.catalog.domain.CatalogWithProducts;
import io.github.spring.middleware.catalog.domain.Product;
import io.github.spring.middleware.catalog.dto.*;
import io.github.spring.middleware.catalog.dto.graphql.CatalogInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogPatchInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogProductInput;
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
        // By default do not expand products; this mirrors a lightweight get
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
    public CatalogDto createCatalog(@GraphQLArgument(name = "input") CatalogInput input) {
        Catalog catalog = catalogMapper.toCatalog(input);
        Catalog created = catalogService.createCatalog(catalog);
        return catalogDtoMapper.toDto(created);
    }

    @GraphQLMutation(name = "replaceCatalog")
    public CatalogDto replaceCatalog(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") CatalogInput input) {
        Catalog catalog = catalogMapper.toCatalog(input);
        Catalog replaced = catalogService.replaceCatalog(id, catalog);
        return catalogDtoMapper.toDto(replaced);
    }

    @GraphQLMutation(name = "patchCatalog")
    public CatalogDto patchCatalog(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") CatalogPatchInput input) {
        Catalog catalog = catalogMapper.toCatalog(input);
        Catalog patched = catalogService.patchCatalog(id, catalog);
        return catalogDtoMapper.toDto(patched);
    }

    @GraphQLMutation(name = "deleteCatalog")
    public Boolean deleteCatalog(@GraphQLArgument(name = "id") UUID id) {
        catalogService.deleteCatalog(id);
        return true;
    }

    private Product mapCatalogProductInputToDomain(CatalogProductInput input) {
        var dto = new ProductInputDto();
        dto.setName(input.getName());
        dto.setSku(input.getSku());
        if (input.getStatus() != null) {
            dto.setStatus(io.github.spring.middleware.catalog.dto.ProductStatusDto.valueOf(input.getStatus().name()));
        }
        var moneyDto = new MoneyDto();
        BigDecimal amount = input.getPriceAmount();
        if (amount != null) {
            moneyDto.setAmount(amount.doubleValue());
        }
        moneyDto.setCurrency(input.getPriceCurrency());
        dto.setPrice(moneyDto);
        return productMapper.toDomain(dto);
    }

    @GraphQLMutation(name = "addProductsToCatalog")
    public CatalogDto addProductsToCatalog(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "inputs") List<CatalogProductInput> inputs) {
        var products = inputs.stream()
                .map(this::mapCatalogProductInputToDomain)
                .toList();
        Catalog catalog = catalogService.addProductsToCatalog(catalogId, products);
        return catalogDtoMapper.toDto(catalog);
    }

    @GraphQLMutation(name = "replaceCatalogProducts")
    public CatalogDto replaceCatalogProducts(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "inputs") List<CatalogProductInput> inputs) {
        var products = inputs.stream()
                .map(this::mapCatalogProductInputToDomain)
                .toList();
        Catalog catalog = catalogService.replaceCatalogProducts(catalogId, products);
        return catalogDtoMapper.toDto(catalog);
    }

    @GraphQLMutation(name = "removeProductsFromCatalog")
    public Boolean removeProductsFromCatalog(
            @GraphQLArgument(name = "catalogId") UUID catalogId,
            @GraphQLArgument(name = "productIds") List<UUID> productIds) {
        catalogService.removeProductsFromCatalog(catalogId, productIds);
        return true;
    }
}
