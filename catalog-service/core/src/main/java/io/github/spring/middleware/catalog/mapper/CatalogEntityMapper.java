package io.github.spring.middleware.catalog.mapper;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.entity.CatalogEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CatalogEntityMapper {

    CatalogEntity toEntity(Catalog catalog);

    Catalog toDomain(CatalogEntity catalogEntity);

}

