package io.github.spring.middleware.product.repository;

import io.github.spring.middleware.product.domain.ProductStatus;
import io.github.spring.middleware.product.entity.BaseProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends MongoRepository<BaseProductEntity, UUID> {

    Page<BaseProductEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<BaseProductEntity> findAllByStatus(ProductStatus status, Pageable pageable);

    Page<BaseProductEntity> findAllByNameContainingIgnoreCaseAndStatus(String name, ProductStatus status, Pageable pageable);

    Page<BaseProductEntity> findAllByCatalogId(UUID catalogId, Pageable pageable);

    Page<BaseProductEntity> findAllByCatalogIdAndStatus(UUID catalogId, ProductStatus status, Pageable pageable);

    Page<BaseProductEntity> findAllByNameContainingIgnoreCaseAndCatalogId(
            String name,
            UUID catalogId,
            Pageable pageable
    );

    Page<BaseProductEntity> findAllByNameContainingIgnoreCaseAndCatalogIdAndStatus(
            String name,
            UUID catalogId,
            ProductStatus status,
            Pageable pageable
    );

    Page<BaseProductEntity> findByCatalogId(UUID catalogId, Pageable pageable);

    boolean existsBySku(String sku);

    BaseProductEntity findBySkuAndCatalogId(String sku, UUID catalogId);

    List<BaseProductEntity> findByIdIn(List<UUID> ids, Sort sorting);
}
