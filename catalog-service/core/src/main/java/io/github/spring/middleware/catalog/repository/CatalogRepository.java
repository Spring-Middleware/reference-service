package io.github.spring.middleware.catalog.repository;

import io.github.spring.middleware.catalog.entity.CatalogEntity;
import io.github.spring.middleware.catalog.domain.CatalogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogRepository extends MongoRepository<CatalogEntity, String> {

    Page<CatalogEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<CatalogEntity> findByStatus(CatalogStatus status, Pageable pageable);

    Page<CatalogEntity> findByNameContainingIgnoreCaseAndStatus(String name, CatalogStatus status, Pageable pageable);
}

