package io.github.spring.middleware.catalog.service;

import io.github.spring.middleware.catalog.entity.CatalogEntity;
import io.github.spring.middleware.catalog.domain.*;
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


}


