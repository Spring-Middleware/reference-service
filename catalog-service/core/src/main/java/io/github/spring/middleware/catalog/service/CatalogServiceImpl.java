package io.github.spring.middleware.catalog.service;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogStatus;
import io.github.spring.middleware.catalog.domain.Product;
import io.github.spring.middleware.catalog.entity.CatalogEntity;
import io.github.spring.middleware.catalog.exception.CatalogNotFoundException;
import io.github.spring.middleware.catalog.mapper.CatalogEntityMapper;
import io.github.spring.middleware.catalog.repository.CatalogRepository;
import io.github.spring.middleware.product.api.ProductsApi;
import io.github.spring.middleware.product.dto.ProductDto;
import io.github.spring.middleware.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;
    private final CatalogEntityMapper catalogEntityMapper;
    private final ProductsApi productsApi;

    public CatalogServiceImpl(final CatalogRepository catalogRepository,
                              final CatalogEntityMapper catalogEntityMapper,
                              final ProductsApi productsApi) {
        this.catalogRepository = catalogRepository;
        this.catalogEntityMapper = catalogEntityMapper;
        this.productsApi = productsApi;
    }


    @Override
    public Catalog createCatalog(Catalog catalog) {
        CatalogEntity catalogEntity = catalogEntityMapper.toEntity(catalog);
        catalogEntity.setId(UUID.randomUUID());
        CatalogEntity savedEntity = catalogRepository.save(catalogEntity);
        return catalogEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Catalog getCatalog(UUID id, boolean expandProducts) {
        CatalogEntity catalogEntity = catalogRepository.findById(id).orElseThrow(() -> new CatalogNotFoundException(STR."Catalog with id \{id} not found"));
        Catalog catalog = catalogEntityMapper.toDomain(catalogEntity);
        if (expandProducts) {
            List<ProductDto> productDtos = PaginationUtils.findAllPages((id,p,s) -> productsApi.listProducts(null, null, id, null, p, s));

        }

        return null;
    }

    @Override
    public Page<Catalog> listCatalogs(String q, CatalogStatus status, Pageable pageable) {
        return null;
    }

    @Override
    public void deleteCatalog(UUID id) {

    }

    @Override
    public Catalog patchCatalog(UUID id, Catalog catalog) {
        return null;
    }

    @Override
    public Catalog replaceCatalog(UUID id, Catalog catalog) {
        return null;
    }

    @Override
    public Page<Product> listCatalogProducts(UUID id, Pageable pageable) {
        return null;
    }

    @Override
    public Catalog addProductsToCatalog(UUID id, List<Product> products) {
        return null;
    }

    @Override
    public Catalog replaceCatalogProducts(UUID id, List<Product> products) {
        return null;
    }

    @Override
    public void removeProductFromCatalog(UUID id, UUID productId) {

    }
}


