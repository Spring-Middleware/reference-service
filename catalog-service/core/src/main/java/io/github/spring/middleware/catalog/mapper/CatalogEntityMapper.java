package io.github.spring.middleware.catalog.mapper;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogWithProducts;
import io.github.spring.middleware.catalog.domain.Product;
import io.github.spring.middleware.catalog.entity.CatalogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CatalogEntityMapper {

    CatalogEntity toEntity(Catalog catalog);

    @Mapping(target = "productIds", source = "productIds")
    Catalog toDomain(CatalogEntity catalogEntity);

    @Mapping(target = "products", source = "productIds", qualifiedByName = "productIdToProduct")
    CatalogWithProducts toWithProducts(CatalogEntity catalogEntity);

    @Named("productIdToProduct")
    default Product productIdToProduct(UUID id) {
        if (id == null) {
            return null;
        }
        Product product = new Product();
        product.setId(id);
        return product;
    }

}
