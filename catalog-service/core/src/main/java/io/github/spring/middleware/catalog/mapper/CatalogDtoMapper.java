package io.github.spring.middleware.catalog.mapper;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogWithProducts;
import io.github.spring.middleware.catalog.dto.CatalogDto;
import io.github.spring.middleware.catalog.dto.CatalogWithProductsDto;
import io.github.spring.middleware.catalog.dto.PagedCatalogResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CatalogDtoMapper {

    @Mapping(target = "productIds", source = "productIds")
    CatalogDto toDto(Catalog catalog);

    @Mapping(target = "products", source = "products")
    CatalogWithProductsDto toWithProductsDto(CatalogWithProducts catalog);

    @Mapping(target = "items", source = "content")
    @Mapping(target = "page", source = "number")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "totalItems", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    PagedCatalogResponseDto toPagedResponseDto(Page<Catalog> catalogPage);

    default JsonNullable<String> map(String value) {
        return JsonNullable.of(value);
    }

    default OffsetDateTime map(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }

}
