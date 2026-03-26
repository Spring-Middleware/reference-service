package io.github.spring.middleware.catalog.service;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogStatus;
import io.github.spring.middleware.catalog.domain.CatalogWithProducts;
import io.github.spring.middleware.catalog.domain.Product;
import io.github.spring.middleware.catalog.domain.ProductType;
import io.github.spring.middleware.catalog.entity.CatalogEntity;
import io.github.spring.middleware.catalog.entity.ProductIdWithType;
import io.github.spring.middleware.catalog.exception.CatalogErrorCodes;
import io.github.spring.middleware.catalog.exception.CatalogNotFoundException;
import io.github.spring.middleware.catalog.kafka.CatalogEvent;
import io.github.spring.middleware.catalog.kafka.CatalogEventType;
import io.github.spring.middleware.catalog.mapper.CatalogEntityMapper;
import io.github.spring.middleware.catalog.mapper.ProductMapper;
import io.github.spring.middleware.catalog.repository.CatalogRepository;
import io.github.spring.middleware.kafka.api.interf.KafkaPublisher;
import io.github.spring.middleware.kafka.core.registry.KafkaPublisherRegistry;
import io.github.spring.middleware.product.api.ProductApi;
import io.github.spring.middleware.product.dto.DigitalProductDto;
import io.github.spring.middleware.product.dto.PhysicalProductDto;
import io.github.spring.middleware.product.dto.ProductBulkCreateRequestDto;
import io.github.spring.middleware.product.dto.ProductBulkDeleteRequestDto;
import io.github.spring.middleware.product.dto.ProductBulkReplaceRequestDto;
import io.github.spring.middleware.product.dto.ProductDto;
import io.github.spring.middleware.utils.PaginationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;
    private final CatalogEntityMapper catalogEntityMapper;
    private final ProductApi productApi;
    private final ProductMapper productMapper;
    private final Optional<KafkaPublisherRegistry> publisherRegistry;

    public CatalogServiceImpl(final CatalogRepository catalogRepository,
                              final CatalogEntityMapper catalogEntityMapper,
                              final ProductMapper productMapper,
                              final ProductApi productApi,
                              final Optional<KafkaPublisherRegistry> publisherRegistry) {
        this.catalogRepository = catalogRepository;
        this.catalogEntityMapper = catalogEntityMapper;
        this.productApi = productApi;
        this.productMapper = productMapper;
        this.publisherRegistry = publisherRegistry;
    }


    @Override
    public Catalog createCatalog(Catalog catalog) {
        CatalogEntity catalogEntity = catalogEntityMapper.toEntity(catalog);
        catalogEntity.setCreatedAt(Instant.now());
        catalogEntity.setUpdatedAt(Instant.now());
        catalogEntity.setId(UUID.randomUUID());
        CatalogEntity savedEntity = catalogRepository.save(catalogEntity);
        Catalog savedCatalog = catalogEntityMapper.toDomain(savedEntity);
        publishCatalogEvent(savedCatalog, CatalogEventType.CREATED);
        return savedCatalog;
    }

    private void publishCatalogEvent(Catalog catalog, CatalogEventType eventType) {
        CatalogEvent catalogEvent = new CatalogEvent();
        catalogEvent.setCatalog(catalog);
        catalogEvent.setEventType(eventType);
        if (publisherRegistry.isEmpty()) {
            log.warn("KafkaPublisherRegistry not available, skipping event publish for catalog: {}", catalog);
            return;
        }
        KafkaPublisher<CatalogEvent, String> publisher = publisherRegistry.get().getPublisher("catalog");
        publisher.publish(catalogEvent).thenAccept(result -> {
            // Log success or handle post-publish actions if needed
            log.info("Published catalog event: {} with result: {}", catalogEvent, result);
        }).exceptionally(ex -> {
            // Log the exception or handle publish failure
            log.error("Failed to publish catalog event: {} due to {}", catalogEvent, ex.getMessage());
            return null;
        });
    }


    @Override
    public CatalogWithProducts getCatalog(UUID id, boolean expandProducts) {
        CatalogEntity catalogEntity = catalogRepository.findById(id).orElseThrow(() -> new CatalogNotFoundException(CatalogErrorCodes.CATALOG_NOT_FOUND, STR."Catalog with id \{id} not found"));
        CatalogWithProducts catalogWithProducts = catalogEntityMapper.toWithProducts(catalogEntity);
        if (expandProducts) {
            List<ProductDto> productDtos = PaginationUtils.findAllPages((catalogId, p, s) -> productApi.listProducts(null, null, catalogId, p, s, null).getItems(), catalogWithProducts.getId(), 0, 100);
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
                .orElseThrow(() -> new CatalogNotFoundException(CatalogErrorCodes.CATALOG_NOT_FOUND, STR."Catalog with id \{id} not found"));
        if (catalogEntity.getProductIdWithTypes() == null || catalogEntity.getProductIdWithTypes().isEmpty()) {
            catalogRepository.deleteById(id);
            return;
        }
        ProductBulkDeleteRequestDto requestDto = new ProductBulkDeleteRequestDto();
        requestDto.setCatalogId(catalogEntity.getId());
        requestDto.setProductIds(catalogEntity.getProductIdWithTypes().stream().map(ProductIdWithType::productId).toList());
        productApi.deleteProducts(requestDto);
        catalogRepository.deleteById(id);
    }

    @Override
    public Catalog patchCatalog(UUID id, Catalog catalog) {
        CatalogEntity entity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(CatalogErrorCodes.CATALOG_NOT_FOUND, STR."Catalog with id \{id} not found"));

        if (catalog.getName() != null) entity.setName(catalog.getName());
        if (catalog.getStatus() != null) entity.setStatus(catalog.getStatus());
        entity.setUpdatedAt(Instant.now());

        CatalogEntity savedEntity = catalogRepository.save(entity);
        return catalogEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Catalog replaceCatalog(UUID id, Catalog catalog) {
        CatalogEntity entity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(CatalogErrorCodes.CATALOG_NOT_FOUND, STR."Catalog with id \{id} not found"));

        CatalogEntity mapped = catalogEntityMapper.toEntity(catalog);
        mapped.setId(id);
        mapped.setCreatedAt(entity.getCreatedAt());
        mapped.setUpdatedAt(Instant.now());
        // Preserve products
        mapped.setProductIdWithTypes(entity.getProductIdWithTypes());

        CatalogEntity savedEntity = catalogRepository.save(mapped);
        return catalogEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Page<Product> listCatalogProducts(UUID id, Pageable pageable) {
        if (!catalogRepository.existsById(id)) {
            throw new CatalogNotFoundException(CatalogErrorCodes.CATALOG_NOT_FOUND, STR."Catalog with id \{id} not found");
        }
        var response = productApi.listProducts(null, null, id, pageable.getPageNumber(), pageable.getPageSize(), null);
        List<Product> products = response.getItems().stream()
                .map(productMapper::toDomain)
                .toList();

        return new PageImpl<>(products, pageable, response.getTotalItems());
    }

    @Override
    public Catalog addProductsToCatalog(UUID id, List<? extends Product> products) {
        final CatalogEntity catalogEntity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(CatalogErrorCodes.CATALOG_NOT_FOUND, STR."Catalog with id \{id} not found"));

        ProductBulkCreateRequestDto requestDto = new ProductBulkCreateRequestDto();
        requestDto.setCatalogId(catalogEntity.getId());
        requestDto.setItems(products.stream().map(productMapper::toCreateItemDto).toList());

        List<ProductDto> productDtos = productApi.createProducts(requestDto);

        // Add new IDs to the existing list
        if (catalogEntity.getProductIdWithTypes() == null) {
            catalogEntity.setProductIdWithTypes(new ArrayList<>());
        }
        productDtos.stream().forEach(productDto -> {
            if (productDto instanceof DigitalProductDto digitalProductDto) {
                catalogEntity.getProductIdWithTypes().add(new ProductIdWithType(digitalProductDto.getId(), ProductType.DIGITAL));
            }
            if (productDto instanceof PhysicalProductDto physicalProductDto) {
                catalogEntity.getProductIdWithTypes().add(new ProductIdWithType(physicalProductDto.getId(), ProductType.PHYSICAL));
            }
        });
        CatalogEntity savedEntity = catalogRepository.save(catalogEntity);
        return catalogEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Catalog replaceCatalogProducts(UUID id, List<? extends Product> products) {
        // Not implemented in this iteration, assumed logical replacement
        final CatalogEntity catalogEntity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(CatalogErrorCodes.CATALOG_NOT_FOUND, STR."Catalog with id \{id} not found"));

        final ProductBulkReplaceRequestDto requestDto = new ProductBulkReplaceRequestDto();
        requestDto.setCatalogId(catalogEntity.getId());
        requestDto.setItems(products.stream().map(productMapper::toReplaceItemDto).toList());
        productApi.replaceProducts(requestDto);
        return catalogEntityMapper.toDomain(catalogEntity);
    }

    @Override
    public void removeProductsFromCatalog(UUID id, List<UUID> productIds) {
        CatalogEntity entity = catalogRepository.findById(id)
                .orElseThrow(() -> new CatalogNotFoundException(CatalogErrorCodes.CATALOG_NOT_FOUND, STR."Catalog with id \{id} not found"));

        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        ProductBulkDeleteRequestDto requestDto = new ProductBulkDeleteRequestDto();
        requestDto.setCatalogId(id);
        requestDto.setProductIds(productIds);
        productApi.deleteProducts(requestDto);
        if (entity.getProductIdWithTypes() != null) {
            entity.getProductIdWithTypes().removeIf(productIdWithType -> productIds.contains(productIdWithType.productId()));
            catalogRepository.save(entity);
        }
    }
}
