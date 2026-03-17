package io.github.spring.middleware.catalog.mapper;

import io.github.spring.middleware.catalog.domain.Catalog;
import io.github.spring.middleware.catalog.domain.CatalogWithProducts;
import io.github.spring.middleware.catalog.domain.DigitalProduct;
import io.github.spring.middleware.catalog.domain.PhysicalProduct;
import io.github.spring.middleware.catalog.domain.Product;
import io.github.spring.middleware.catalog.domain.ProductType;
import io.github.spring.middleware.catalog.entity.CatalogEntity;
import io.github.spring.middleware.catalog.entity.ProductIdWithType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CatalogEntityMapper {

    CatalogEntity toEntity(Catalog catalog);

    @Mapping(target = "productIds", source = "productIdWithTypes", qualifiedByName = "productToProductIdWithType")
    Catalog toDomain(CatalogEntity catalogEntity);

    @Named("productToProductIdWithType")
    default List<UUID> productToProductIdWithType(List<ProductIdWithType> productIdWithTypes) {
        return productIdWithTypes.stream().map(ProductIdWithType::productId).toList();
    }

    @Mapping(target = "products", source = "productIdWithTypes", qualifiedByName = "productIdWithTypesToProduct")
    CatalogWithProducts toWithProducts(CatalogEntity catalogEntity);

    @Named("productIdWithTypesToProduct")
    default Product productIdWithTypesToProduct(ProductIdWithType productIdWithType) {
        if (productIdWithType == null) {
            return null;
        }
        if (productIdWithType.productType() == null || productIdWithType.productId() == null) {
            return null;
        }
        Product product = null;
        ProductType productType = productIdWithType.productType();
        if (productType == ProductType.PHYSICAL) {
            product = new PhysicalProduct();
        } else if (productType == ProductType.DIGITAL) {
            product = new DigitalProduct();
        }
        product.setId(productIdWithType.productId());
        return product;
    }


}
