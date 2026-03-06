package io.github.spring.middleware.catalog.service;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogStatus;
import io.github.spring.middleware.catalog.domain.CatalogWithProducts;
import io.github.spring.middleware.catalog.domain.Product;
import io.github.spring.middleware.catalog.entity.CatalogEntity;
import io.github.spring.middleware.catalog.exception.CatalogNotFoundException;
import io.github.spring.middleware.catalog.mapper.CatalogEntityMapper;
import io.github.spring.middleware.catalog.mapper.ProductMapper;
import io.github.spring.middleware.catalog.repository.CatalogRepository;
import io.github.spring.middleware.product.api.ProductsApi;
import io.github.spring.middleware.product.dto.ProductBulkCreateRequestDto;
import io.github.spring.middleware.product.dto.ProductBulkDeleteRequestDto;
import io.github.spring.middleware.product.dto.ProductBulkReplaceRequestDto;
import io.github.spring.middleware.product.dto.ProductDto;
import io.github.spring.middleware.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;
    private final CatalogEntityMapper catalogEntityMapper;
    private final ProductsApi productsApi;
    private final ProductMapper productMapper;

    public CatalogServiceImpl(final CatalogRepository catalogRepository,
                              final CatalogEntityMapper catalogEntityMapper,
                              final ProductMapper productMapper,
                              final ProductsApi productsApi) {
        this.catalogRepository = catalogRepository;
        this.catalogEntityMapper = catalogEntityMapper;
        this.productsApi = productsApi;
        this.productMapper = productMapper;
    }


    @Override
    public Catalog createCatalog(Catalog catalog) {
        CatalogEntity catalogEntity = catalogEntityMapper.toEntity(catalog);
        catalogEntity.setCreatedAt(Instant.now());
        catalogEntity.setUpdatedAt(Instant.now());
        catalogEntity.setId(UUID.randomUUID());
        CatalogEntity savedEntity = catalogRepository.save(catalogEntity);
        return catalogEntityMapper.toDomain(savedEntity);
    }

    @Override
    public CatalogWithProducts getCatalog(UUID id, boolean expandProducts) {
        CatalogEntity catalogEntity = catalogRepository.findById(id).orElseThrow(() -> new CatalogNotFoundException(STR."Catalog with id \{id} not found"));
        CatalogWithProducts catalogWithProducts = catalogEntityMapper.toWithProducts(catalogEntity);
        if (expandProducts) {
            List<ProductDto> productDtos = PaginationUtils.findAllPages((catalogId, p, s) -> productsApi.listProducts(null, null, catalogId, p, s, null).getItems(), catalogWithProducts.getId(), 0, 100);
            catalogWithProducts.setProducts(productDtos.stream().map(productMapper::toDomain).toList());
        }
        return catalogWithProducts;
    }

    @Override
    public Page<Catalog> listCatalogs(String q, CatalogStatus status, Pageable pageable) {
        Page<CatalogEntity> page;
        if (q != null && !q.isBlank()) {
            if (status != null) {
                page = catalogRepository.findByNameContainingIgnoreCaseAndStatus(q, status, pageable);
            } else {
                page = catalogRepository.findByNameContainingIgnoreCase(q, pageable);
            }
        } else {
            if (status != null) {
                page = catalogRepository.findByStatus(status, pageable);
            } else {
                page = catalogRepository.findAll(pageable);
            }
        }
        return page.map(catalogEntityMapper::toDomain);
    }

    @Override
    public void deleteCatalog(UUID id) {
        CatalogEntity catalogEntity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(STR."Catalog with id \{id} not found"));
        if (catalogEntity.getProductIds() == null || catalogEntity.getProductIds().isEmpty()) {
            catalogRepository.deleteById(id);
            return;
        }
        ProductBulkDeleteRequestDto requestDto = new ProductBulkDeleteRequestDto();
        requestDto.setCatalogId(catalogEntity.getId());
        requestDto.setProductIds(catalogEntity.getProductIds());
        productsApi.deleteProducts(requestDto);
        catalogRepository.deleteById(id);
    }

    @Override
    public Catalog patchCatalog(UUID id, Catalog catalog) {
        CatalogEntity entity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(STR."Catalog with id \{id} not found"));

        if (catalog.getName() != null) entity.setName(catalog.getName());
        if (catalog.getStatus() != null) entity.setStatus(catalog.getStatus());
        entity.setUpdatedAt(Instant.now());

        CatalogEntity savedEntity = catalogRepository.save(entity);
        return catalogEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Catalog replaceCatalog(UUID id, Catalog catalog) {
        CatalogEntity entity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(STR."Catalog with id \{id} not found"));

        CatalogEntity mapped = catalogEntityMapper.toEntity(catalog);
        mapped.setId(id);
        mapped.setCreatedAt(entity.getCreatedAt());
        mapped.setUpdatedAt(Instant.now());
        // Preserve products
        mapped.setProductIds(entity.getProductIds());

        CatalogEntity savedEntity = catalogRepository.save(mapped);
        return catalogEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Page<Product> listCatalogProducts(UUID id, Pageable pageable) {
        if (!catalogRepository.existsById(id)) {
            throw new CatalogNotFoundException(STR."Catalog with id \{id} not found");
        }
        var response = productsApi.listProducts(null, null, id, pageable.getPageNumber(), pageable.getPageSize(), null);
        List<Product> products = response.getItems().stream()
                .map(productMapper::toDomain)
                .toList();

        return new PageImpl<>(products, pageable, response.getTotalItems());
    }

    @Override
    public Catalog addProductsToCatalog(UUID id, List<Product> products) {
        final CatalogEntity catalogEntity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(STR."Catalog with id \{id} not found"));

        ProductBulkCreateRequestDto requestDto = new ProductBulkCreateRequestDto();
        requestDto.setCatalogId(catalogEntity.getId());
        requestDto.setItems(products.stream().map(productMapper::toCreateItemDto).toList());

        List<ProductDto> productDtos = productsApi.createProducts(requestDto);

        // Add new IDs to the existing list
        List<UUID> newIds = productDtos.stream().map(ProductDto::getId).toList();
        if (catalogEntity.getProductIds() == null) {
            catalogEntity.setProductIds(new java.util.ArrayList<>());
        }
        catalogEntity.getProductIds().addAll(newIds);

        CatalogEntity savedEntity = catalogRepository.save(catalogEntity);
        return catalogEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Catalog replaceCatalogProducts(UUID id, List<Product> products) {
        // Not implemented in this iteration, assumed logical replacement
        final CatalogEntity catalogEntity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(STR."Catalog with id \{id} not found"));

        final ProductBulkReplaceRequestDto requestDto = new ProductBulkReplaceRequestDto();
        requestDto.setCatalogId(catalogEntity.getId());
        requestDto.setItems(products.stream().map(productMapper::toReplaceItemDto).toList());
        productsApi.replaceProducts(requestDto);
        return catalogEntityMapper.toDomain(catalogEntity);
    }

    @Override
    public void removeProductsFromCatalog(UUID id, List<UUID> productIds) {
        CatalogEntity entity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(STR."Catalog with id \{id} not found"));

        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        ProductBulkDeleteRequestDto requestDto = new ProductBulkDeleteRequestDto();
        requestDto.setCatalogId(id);
        requestDto.setProductIds(productIds);
        productsApi.deleteProducts(requestDto);
        if (entity.getProductIds() != null) {
            entity.getProductIds().removeAll(productIds);
            catalogRepository.save(entity);
        }
    }
}
