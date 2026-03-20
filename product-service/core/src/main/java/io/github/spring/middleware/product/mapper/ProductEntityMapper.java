package io.github.spring.middleware.product.mapper;

import io.github.spring.middleware.product.domain.DigitalProduct;
import io.github.spring.middleware.product.domain.PhysicalProduct;
import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.domain.ProductType;
import io.github.spring.middleware.product.entity.BaseProductEntity;
import io.github.spring.middleware.product.entity.DigitalProductEntity;
import io.github.spring.middleware.product.entity.PhysicalProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    default BaseProductEntity toEntity(Product product) {
        if (product == null) {
            return null;
        }
        ProductType type = product.getProductType();
        if (type == ProductType.DIGITAL) {
            return toDigitalEntity((DigitalProduct) product);
        }
        // por defecto, tratamos como físico
        return toPhysicalEntity((PhysicalProduct) product);
    }

    PhysicalProductEntity toPhysicalEntity(PhysicalProduct product);

    DigitalProductEntity toDigitalEntity(DigitalProduct product);

    default Product toDomain(BaseProductEntity entity) {
        if (entity == null) {
            return null;
        }
        ProductType type = entity.getProductType();
        if (type == ProductType.DIGITAL) {
            return toDigitalDomain((DigitalProductEntity) entity);
        }
        // por defecto, físico
        return toPhysicalDomain((PhysicalProductEntity) entity);
    }


    PhysicalProduct toPhysicalDomain(PhysicalProductEntity entity);

    DigitalProduct toDigitalDomain(DigitalProductEntity entity);
}
