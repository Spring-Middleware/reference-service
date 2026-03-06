package io.github.spring.middleware.catalog.mapper;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.dto.CatalogCreateRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogPatchRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogUpdateRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CatalogMapper {

    Catalog toCatalog(CatalogCreateRequestDto catalogCreateRequestDto);

    Catalog toCatalog(CatalogUpdateRequestDto catalogUpdateRequestDto);

    Catalog toCatalog(CatalogPatchRequestDto catalogPatchRequestDto);

}
