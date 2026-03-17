package io.github.spring.middleware.catalog.mapper;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.DigitalProduct;
import io.github.spring.middleware.catalog.domain.PhysicalProduct;
import io.github.spring.middleware.catalog.dto.CatalogCreateRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogPatchRequestDto;
import io.github.spring.middleware.catalog.dto.CatalogUpdateRequestDto;
import io.github.spring.middleware.catalog.dto.graphql.CatalogDigitalProductInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogDigitalProductUpdateInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogPatchInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogPhysicalProductInput;
import io.github.spring.middleware.catalog.dto.graphql.CatalogPhysicalProductUpdateInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CatalogMapper {

    Catalog toCatalog(CatalogCreateRequestDto catalogCreateRequestDto);

    Catalog toCatalog(CatalogUpdateRequestDto catalogUpdateRequestDto);

    Catalog toCatalog(CatalogPatchRequestDto catalogPatchRequestDto);

    // New GraphQL input mappings
    Catalog toCatalog(CatalogInput input);

    Catalog toCatalog(CatalogPatchInput input);

    @Mapping(target = "price.amount", source = "input.priceAmount")
    @Mapping(target = "price.currency", source = "input.priceCurrency")
    DigitalProduct toDigitalProduct(CatalogDigitalProductInput input);

    @Mapping(target = "price.amount", source = "input.priceAmount")
    @Mapping(target = "price.currency", source = "input.priceCurrency")
    DigitalProduct toDigitalProduct(CatalogDigitalProductUpdateInput input);

    @Mapping(target = "price.amount", source = "input.priceAmount")
    @Mapping(target = "price.currency", source = "input.priceCurrency")
    PhysicalProduct toPhysicalProduct(CatalogPhysicalProductInput input);

    @Mapping(target = "price.amount", source = "input.priceAmount")
    @Mapping(target = "price.currency", source = "input.priceCurrency")
    PhysicalProduct toPhysicalProduct(CatalogPhysicalProductUpdateInput input);

}
