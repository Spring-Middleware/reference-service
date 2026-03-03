package io.github.spring.middleware.catalog.controller;

import io.github.spring.middleware.annotation.RegisterSchema;
import io.github.spring.middleware.catalog.api.CatalogsApi;
import io.github.spring.middleware.catalog.dto.*;
import io.github.spring.middleware.catalog.model.*;

import io.github.spring.middleware.catalog.service.CatalogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RegisterSchema("catalog")
public class CatalogController implements CatalogsApi {

    @Override
    public CatalogDto addProductsToCatalog(String id, CatalogProductsAddRequestDto catalogProductsAddRequestDto) {
        return null;
    }

    @Override
    public CatalogDto createCatalog(CatalogCreateRequestDto catalogCreateRequestDto) {
        return null;
    }

    @Override
    public void deleteCatalog(String id) {

    }

    @Override
    public CatalogDto getCatalog(String id, String expand) {
        return null;
    }

    @Override
    public PagedProductResponseDto listCatalogProducts(UUID id, Integer page, Integer size) {
        return null;
    }

    @Override
    public void listCatalogs(String q, CatalogStatusDto status, Integer page, Integer size, String sort) {

    }

    @Override
    public CatalogDto patchCatalog(String id, CatalogPatchRequestDto catalogPatchRequestDto) {
        return null;
    }

    @Override
    public void removeProductFromCatalog(String id, String productId) {

    }

    @Override
    public CatalogDto replaceCatalog(String id, CatalogUpdateRequestDto catalogUpdateRequestDto) {
        return null;
    }

    @Override
    public CatalogDto replaceCatalogProducts(String id, CatalogProductsReplaceRequestDto catalogProductsReplaceRequestDto) {
        return null;
    }
}
