package io.github.spring.middleware.product.service;

import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.domain.ProductStatus;
import io.github.spring.middleware.product.entity.ProductEntity;
import io.github.spring.middleware.product.exceptions.ProductAlreadyExistsException;
import io.github.spring.middleware.product.exceptions.ProductNotFoundException;
import io.github.spring.middleware.product.mapper.ProductEntityMapper;
import io.github.spring.middleware.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        if (productRepository.existsBySku(product.getSku())) {
            throw new ProductAlreadyExistsException(STR."Product with SKU \{product.getSku()} already exists");
        }
        ProductEntity entity = productEntityMapper.toEntity(product);

        // Generate UUID manually to ensure it is stored as UUID in MongoDB, not ObjectId
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        ProductEntity saved = productRepository.save(entity);
        return productEntityMapper.toDomain(saved);
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
    public Product patchProduct(UUID id, Product product) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(STR."Product with \{id} not found"));

        if (product.getSku() != null) {
            if (!entity.getSku().equals(product.getSku()) && productRepository.existsBySku(product.getSku())) {
                throw new ProductAlreadyExistsException(STR."Product with SKU \{product.getSku()} already exists");
            }
            entity.setSku(product.getSku());
        }
        if (product.getName() != null) entity.setName(product.getName());
        if (product.getDescription() != null) entity.setDescription(product.getDescription());
        if (product.getStatus() != null) entity.setStatus(product.getStatus());
        if (product.getPrice() != null) entity.setPrice(product.getPrice());

        entity.setUpdatedAt(Instant.now());

        ProductEntity saved = productRepository.save(entity);
        return productEntityMapper.toDomain(saved);
    }

    @Override
    public Page<Product> listProducts(String q, ProductStatus status, Pageable pageable) {
        Page<ProductEntity> page;
        if (q != null && !q.isBlank()) {
            page = productRepository.findByNameContainingIgnoreCase(q, pageable);
        } else {
            page = productRepository.findAll(pageable);
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
    public List<Product> lookupProducts(List<UUID> ids) {
        List<ProductEntity> entities = productRepository.findAllById(ids);

        return entities.stream()
                .map(productEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
