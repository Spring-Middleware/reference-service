package io.github.spring.middleware.catalog.controller;

import io.github.spring.middleware.annotation.Register;
import io.github.spring.middleware.annotation.RegisterSchema;
import io.github.spring.middleware.catalog.api.CatalogApi;
import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogStatus;
import io.github.spring.middleware.catalog.domain.CatalogWithProducts;
import io.github.spring.middleware.catalog.dto.CatalogCreateRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogDto;
import io.github.spring.middleware.catalog.dto.CatalogPatchRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogProductsAddRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogProductsRemoveRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogProductsReplaceRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogStatusDto;
import io.github.spring.middleware.catalog.dto.CatalogUpdateRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogWithProductsDto;
import io.github.spring.middleware.catalog.dto.PagedCatalogResponseDto;
import io.github.spring.middleware.catalog.dto.PagedProductResponseDto;
import io.github.spring.middleware.catalog.mapper.CatalogDtoMapper;
import io.github.spring.middleware.catalog.mapper.CatalogMapper;
import io.github.spring.middleware.catalog.mapper.ProductMapper;
import io.github.spring.middleware.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static io.github.spring.middleware.utils.PageRequestUtils.buildPageRequest;

@Slf4j
@RestController
@Register(name = "catalog")
@RegisterSchema("catalog")
@RequiredArgsConstructor
public class CatalogController implements CatalogApi {

    private final CatalogService catalogService;

    private final CatalogMapper catalogMapper;

    private final CatalogDtoMapper catalogDtoMapper;

    private final ProductMapper productMapper;

    @Override
    public CatalogDto addProductsToCatalog(UUID id, CatalogProductsAddRequestDto catalogProductsAddRequestDto) {
        log.info("Received request to add products to catalog with id: {}", id);
        var products = catalogProductsAddRequestDto.getProducts().stream()
                .map(productMapper::toDomain)
                .toList();
        Catalog catalog = catalogService.addProductsToCatalog(id, products);
        return catalogDtoMapper.toDto(catalog);
    }

    @Override
    public CatalogDto createCatalog(CatalogCreateRequestDto catalogCreateRequestDto) {
        log.info("Received request to create catalog with name: {}", catalogCreateRequestDto.getName());
        Catalog catalog = catalogMapper.toCatalog(catalogCreateRequestDto);
        Catalog createdCatalog = catalogService.createCatalog(catalog);
        return catalogDtoMapper.toDto(createdCatalog);
    }

    @Override
    public void deleteCatalog(UUID id) {
        log.info("Received request to delete catalog with id: {}", id);
        catalogService.deleteCatalog(id);
    }

    @Override
    public CatalogWithProductsDto getCatalog(UUID id, String expand) {
        log.info("Received request to get catalog with id: {}", id);
        CatalogWithProducts catalog = catalogService.getCatalog(id, "products".equals(expand));
        return catalogDtoMapper.toWithProductsDto(catalog);
    }

    @Override
    public PagedProductResponseDto listCatalogProducts(UUID id, Integer page, Integer size) {
        log.info("Received request to list products of catalog with id: {}", id);
        var pagedProducts = catalogService.listCatalogProducts(id, PageRequest.of(page, size));
        return productMapper.toPagedResponseDto(pagedProducts);
    }

    @Override
    public PagedCatalogResponseDto listCatalogs(String q, CatalogStatusDto status, Integer page, Integer size, String sort) {
        log.info("Received request to list catalogs with query: {}, status: {}, page: {}, size: {}, sort: {}", q, status, page, size, sort);
        PageRequest pageRequest = buildPageRequest(page, size, sort);
        var pagedCatalogs = catalogService.listCatalogs(
                q,
                status != null ? CatalogStatus.valueOf(status.name()) : null,
                pageRequest
        );
        return catalogDtoMapper.toPagedResponseDto(pagedCatalogs);
    }


    @Override
    public CatalogDto patchCatalog(UUID id, CatalogPatchRequestDto catalogPatchRequestDto) {
        log.info("Received request to patch catalog with id: {}", id);
        Catalog catalog = catalogMapper.toCatalog(catalogPatchRequestDto);
        catalog = catalogService.patchCatalog(id, catalog);
        return catalogDtoMapper.toDto(catalog);
    }

    @Override
    public void removeProductsFromCatalog(UUID id, CatalogProductsRemoveRequestDto catalogProductsRemoveRequestDto) {
        catalogService.removeProductsFromCatalog(id, catalogProductsRemoveRequestDto.getProductIds());
    }

    @Override
    public CatalogDto replaceCatalog(UUID id, CatalogUpdateRequestDto catalogUpdateRequestDto) {
        log.info("Received request to replace catalog with id: {}", id);
        Catalog catalog = catalogMapper.toCatalog(catalogUpdateRequestDto);
        catalog = catalogService.replaceCatalog(id, catalog);
        return catalogDtoMapper.toDto(catalog);
    }

    @Override
    public CatalogDto replaceCatalogProducts(UUID id, CatalogProductsReplaceRequestDto catalogProductsReplaceRequestDto) {
        log.info("Received request to replace products of catalog with id: {}", id);
        var products = catalogProductsReplaceRequestDto.getProducts().stream()
                .map(productMapper::toDomain)
                .toList();
        Catalog catalog = catalogService.replaceCatalogProducts(id, products);
        return catalogDtoMapper.toDto(catalog);
    }
}
