package io.github.spring.middleware.catalog.controller;

import io.github.spring.middleware.annotation.RegisterSchema;
import io.github.spring.middleware.catalog.api.CatalogsApi;
import io.github.spring.middleware.catalog.dto.*;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RegisterSchema("catalog")
public class CatalogController implements CatalogsApi {


    @Override
    public CatalogDto addProductsToCatalog(UUID id, CatalogProductsAddRequestDto catalogProductsAddRequestDto) {
        return null;
    }

    @Override
    public CatalogDto createCatalog(CatalogCreateRequestDto catalogCreateRequestDto) {
        return null;
    }

    @Override
    public void deleteCatalog(UUID id) {

    }

    @Override
    public CatalogDto getCatalog(UUID id, String expand) {
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
    public CatalogDto patchCatalog(UUID id, CatalogPatchRequestDto catalogPatchRequestDto) {
        return null;
    }

    @Override
    public void removeProductFromCatalog(UUID id, UUID productId) {

    }

    @Override
    public CatalogDto replaceCatalog(UUID id, CatalogUpdateRequestDto catalogUpdateRequestDto) {
        return null;
    }

    @Override
    public CatalogDto replaceCatalogProducts(UUID id, CatalogProductsReplaceRequestDto catalogProductsReplaceRequestDto) {
        return null;
    }
}
