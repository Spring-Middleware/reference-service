package io.github.spring.middleware.product.mapper;

import io.github.spring.middleware.product.domain.DigitalProduct;
import io.github.spring.middleware.product.domain.DigitalProductWithReviews;
import io.github.spring.middleware.product.domain.PhysicalProduct;
import io.github.spring.middleware.product.domain.PhysicalProductWithReviews;
import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.domain.ProductType;
import io.github.spring.middleware.product.domain.ProductWithReviews;
import io.github.spring.middleware.product.domain.Review;
import io.github.spring.middleware.product.entity.BaseProductEntity;
import io.github.spring.middleware.product.entity.DigitalProductEntity;
import io.github.spring.middleware.product.entity.PhysicalProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

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

    default ProductWithReviews toDomainWithReviews(BaseProductEntity entity) {
        if (entity == null) {
            return null;
        }
        ProductType type = entity.getProductType();
        if (type == ProductType.DIGITAL) {
            return toDigitalDomainWithReviews((DigitalProductEntity) entity);
        }
        // por defecto, físico
        return toPhysicalDomainWithReviews((PhysicalProductEntity) entity);
    }

    @Named("mapReviewIdsToReviews")
    default List<Review> mapReviewIdsToReviews(List<UUID> reviewIds) {
        if (reviewIds == null) {
            return null;
        }
        return reviewIds.stream().map(id ->
                Review.builder()
                        .id(id)
                        .build()
        ).toList();
    }

    PhysicalProduct toPhysicalDomain(PhysicalProductEntity entity);

    DigitalProduct toDigitalDomain(DigitalProductEntity entity);

    @Mapping(target = "reviews", source = "reviewIds", qualifiedByName = "mapReviewIdsToReviews")
    PhysicalProductWithReviews toPhysicalDomainWithReviews(PhysicalProductEntity entity);

    @Mapping(target = "reviews", source = "reviewIds", qualifiedByName = "mapReviewIdsToReviews")
    DigitalProductWithReviews toDigitalDomainWithReviews(DigitalProductEntity entity);
}
