package io.github.spring.middleware.product.service;

import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.domain.ProductStatus;
import io.github.spring.middleware.product.entity.ProductEntity;
import io.github.spring.middleware.product.exceptions.ProductAlreadyExistsException;
import io.github.spring.middleware.product.exceptions.ProductNotFoundException;
import io.github.spring.middleware.product.mapper.ProductEntityMapper;
import io.github.spring.middleware.product.repository.ProductRepository;
import io.github.spring.middleware.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductEntityMapper productEntityMapper) {
        this.productRepository = productRepository;
        this.productEntityMapper = productEntityMapper;
    }

    @Override
    public Product createProduct(Product product) {
        return createProductsForCatalog(List.of(product), product.getCatalogId()).getFirst();
    }

    @Override
    public List<Product> createProductsForCatalog(List<Product> products, UUID catalogId) {
        List<ProductEntity> entities = products.stream().map(product -> {
            if (productRepository.existsBySku(product.getSku())) {
                throw new ProductAlreadyExistsException(STR."Product with SKU \{product.getSku()} already exists");
            }
            ProductEntity entity = productEntityMapper.toEntity(product);
            entity.setCatalogId(catalogId);
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            Instant now = Instant.now();
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            return entity;
        }).collect(Collectors.toList());

        List<ProductEntity> savedEntities = productRepository.saveAll(entities);
        return savedEntities.stream()
                .map(productEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Product getProduct(UUID id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(STR."Product with \{id} not found"));
        return productEntityMapper.toDomain(entity);
    }

    @Override
    public Product replaceProduct(UUID id, Product product) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(STR."Product with \{id} not found"));

        if (!entity.getSku().equals(product.getSku()) && productRepository.existsBySku(product.getSku())) {
            throw new ProductAlreadyExistsException(STR."Product with SKU \{product.getSku()} already exists");
        }

        ProductEntity mappedEntity = productEntityMapper.toEntity(product);
        mappedEntity.setId(id); // Ensure ID is preserved
        mappedEntity.setCreatedAt(entity.getCreatedAt()); // Preserve creation date
        mappedEntity.setUpdatedAt(Instant.now());

        ProductEntity saved = productRepository.save(mappedEntity);
        return productEntityMapper.toDomain(saved);
    }

    @Override
    public List<Product> replaceProductsForCatalog(List<Product> products, UUID catalogId) {
        List<ProductEntity> existingEntities = PaginationUtils.findAllPages((id, page, size) -> productRepository.findByCatalogId(id, PageRequest.of(page, size)).getContent(), catalogId,0, 100);
        List<UUID> existingIds = existingEntities.stream().map(ProductEntity::getId).collect(Collectors.toList());

        List<ProductEntity> entitiesToSave = products.stream().map(product -> {
            if (product.getId() != null && !existingIds.contains(product.getId())) {
                throw new ProductNotFoundException(STR."Product with ID \{product.getId()} not found in catalog \{catalogId}");
            }
            if (productRepository.existsBySkuAndCatalogId(product.getSku(), catalogId)) {
                throw new ProductAlreadyExistsException(STR."Product with SKU \{product.getSku()} already exists in catalog \{catalogId}");
            }
            ProductEntity entity = productEntityMapper.toEntity(product);
            entity.setCatalogId(catalogId);
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
                entity.setCreatedAt(Instant.now());
            } else {
                entity.setCreatedAt(existingEntities.stream()
                        .filter(e -> e.getId().equals(entity.getId()))
                        .findFirst()
                        .map(ProductEntity::getCreatedAt)
                        .orElse(Instant.now()));
            }
            entity.setUpdatedAt(Instant.now());
            return entity;
        }).collect(Collectors.toList());

        List<ProductEntity> savedEntities = productRepository.saveAll(entitiesToSave);
        return savedEntities.stream()
                .map(productEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Product patchProduct(UUID id, Product product) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(STR."Product with \{id} not found"));

        if (product.getSku() != null) {
            if (!entity.getSku().equals(product.getSku()) && productRepository.existsBySku(product.getSku())) {
                throw new ProductAlreadyExistsException(STR."Product with SKU \{product.getSku()} already exists");
            }
            entity.setSku(product.getSku());
        }
        // catalogId cannot be changed
        if (product.getName() != null) entity.setName(product.getName());
        if (product.getStatus() != null) entity.setStatus(product.getStatus());
        if (product.getPrice() != null) entity.setPrice(product.getPrice());

        entity.setUpdatedAt(Instant.now());

        ProductEntity saved = productRepository.save(entity);
        return productEntityMapper.toDomain(saved);
    }

    @Override
    public Page<Product> listProducts(String q, ProductStatus status, UUID catalogId, Pageable pageable) {
        Page<ProductEntity> page;
        if (q != null && !q.isBlank()) {
            if (catalogId != null) {
                page = productRepository.findByNameContainingIgnoreCaseAndCatalogId(q, catalogId, pageable);
            } else {
                page = productRepository.findByNameContainingIgnoreCase(q, pageable);
            }
        } else {
            if (catalogId != null) {
                page = productRepository.findByCatalogId(catalogId, pageable);
            } else {
                page = productRepository.findAll(pageable);
            }
        }

        List<Product> products = page.getContent().stream()
                .map(productEntityMapper::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(products, pageable, page.getTotalElements());
    }

    @Override
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(STR."Product with \{id} not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public void deleteProductsFromCatalog(List<UUID> ids, UUID catalogId) {
        List<UUID> existingIds = productRepository.findAllById(ids).stream()
                .filter(productEntity -> productEntity.getCatalogId().equals(catalogId))
                .map(ProductEntity::getId)
                .collect(Collectors.toList());

        List<UUID> notFoundIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());
        if (!notFoundIds.isEmpty()) {
            throw new ProductNotFoundException(STR."Products with IDs \{notFoundIds} not found in catalog \{catalogId}");
        }
        productRepository.deleteAllById(ids);
    }
}
