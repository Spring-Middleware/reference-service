package io.github.spring.middleware.product.repository;

import io.github.spring.middleware.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, UUID> {

    Page<ProductEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsBySku(String sku);
}

