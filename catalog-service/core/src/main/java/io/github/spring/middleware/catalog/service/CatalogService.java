package io.github.spring.middleware.catalog.service;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogStatus;
import io.github.spring.middleware.catalog.domain.CatalogWithProducts;
import io.github.spring.middleware.catalog.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CatalogService {

    Catalog createCatalog(Catalog catalog);

    CatalogWithProducts getCatalog(UUID id, boolean expandProducts);

    Page<Catalog> listCatalogs(String q, CatalogStatus status, Pageable pageable);

    void deleteCatalog(UUID id);

    Catalog patchCatalog(UUID id, Catalog catalog);

    Catalog replaceCatalog(UUID id, Catalog catalog);

    Page<Product> listCatalogProducts(UUID id, Pageable pageable);

    Catalog addProductsToCatalog(UUID id, List<? extends Product> products);

    Catalog replaceCatalogProducts(UUID id, List<? extends Product> products);

    void removeProductsFromCatalog(UUID id, List<UUID> productIds);
}
