package io.github.spring.middleware.catalog.service;

import io.github.spring.middleware.catalog.entity.CatalogEntity;
import io.github.spring.middleware.catalog.model.*;
import io.github.spring.middleware.catalog.repository.CatalogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogServiceImpl(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Override
    public CatalogEntity createCatalog(CatalogCreateRequest request) {
        CatalogEntity entity = new CatalogEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setName(request.getName());
        entity.setStatus(request.getStatus());

        CatalogEntity saved = catalogRepository.save(entity);
        return mapToCatalog(saved);
    }

    @Override
    public CatalogEntity getCatalog(String id) {
        CatalogEntity entity = catalogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog not found with id: " + id));
        return mapToCatalog(entity);
    }

    @Override
    public void deleteCatalog(String id) {
        if (!catalogRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog not found with id: " + id);
        }
        catalogRepository.deleteById(id);
    }

    @Override
    public PagedCatalogResponse listCatalogs(String q, CatalogStatus status, Pageable pageable) {
        Page<CatalogEntity> page;

        if (q != null && status != null) {
            page = catalogRepository.findByNameContainingIgnoreCaseAndStatus(q, status, pageable);
        } else if (q != null) {
            page = catalogRepository.findByNameContainingIgnoreCase(q, pageable);
        } else if (status != null) {
            page = catalogRepository.findByStatus(status, pageable);
        } else {
            page = catalogRepository.findAll(pageable);
        }

        List<CatalogEntity> items = page.getContent().stream()
                .map(this::mapToCatalog)
                .collect(Collectors.toList());

        PagedCatalogResponse response = new PagedCatalogResponse();
        response.setItems(items);
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalItems((int) page.getTotalElements());
        response.setTotalPages(page.getTotalPages());

        return response;
    }

    @Override
    public CatalogEntity replaceCatalog(String id, CatalogUpdateRequest request) {
        CatalogEntity entity = catalogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog not found with id: " + id));

        entity.setName(request.getName());
        entity.setStatus(request.getStatus());

        CatalogEntity saved = catalogRepository.save(entity);
        return mapToCatalog(saved);
    }

    @Override
    public CatalogEntity patchCatalog(String id, CatalogPatchRequest request) {
        CatalogEntity entity = catalogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog not found with id: " + id));

        if (request.getName() != null && request.getName().isPresent()) {
            entity.setName(request.getName().get());
        }

        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        CatalogEntity saved = catalogRepository.save(entity);
        return mapToCatalog(saved);
    }

    private CatalogEntity mapToCatalog(CatalogEntity entity) {
        CatalogEntity catalog = new CatalogEntity();
        catalog.setId(entity.getId());
        catalog.setName(entity.getName());
        catalog.setStatus(entity.getStatus());
        catalog.setCreatedAt(entity.getCreatedAt());
        catalog.setUpdatedAt(entity.getUpdatedAt());
        return catalog;
    }
}


