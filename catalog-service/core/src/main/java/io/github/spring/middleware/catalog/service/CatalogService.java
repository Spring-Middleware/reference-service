package io.github.spring.middleware.catalog.service;

import io.github.spring.middleware.catalog.entity.CatalogEntity;
import io.github.spring.middleware.catalog.model.CatalogCreateRequest;
import io.github.spring.middleware.catalog.model.CatalogPatchRequest;
import io.github.spring.middleware.catalog.model.CatalogStatus;
import io.github.spring.middleware.catalog.model.CatalogUpdateRequest;
import io.github.spring.middleware.catalog.model.PagedCatalogResponse;
import org.springframework.data.domain.Pageable;

public interface CatalogService {

    CatalogEntity createCatalog(CatalogCreateRequest request);

    CatalogEntity getCatalog(String id);

    void deleteCatalog(String id);

    PagedCatalogResponse listCatalogs(String q, CatalogStatus status, Pageable pageable);

    CatalogEntity replaceCatalog(String id, CatalogUpdateRequest request);

    CatalogEntity patchCatalog(String id, CatalogPatchRequest request);
}

