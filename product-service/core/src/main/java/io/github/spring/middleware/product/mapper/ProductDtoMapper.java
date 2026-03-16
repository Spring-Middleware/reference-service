package io.github.spring.middleware.product.mapper;

import io.github.spring.middleware.product.domain.DigitalProduct;
import io.github.spring.middleware.product.domain.Money;
import io.github.spring.middleware.product.domain.PhysicalProduct;
import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.domain.ProductStatus;
import io.github.spring.middleware.product.dto.DigitalProductDto;
import io.github.spring.middleware.product.dto.MoneyDto;
import io.github.spring.middleware.product.dto.PhysicalProductDto;
import io.github.spring.middleware.product.dto.ProductDto;
import io.github.spring.middleware.product.dto.ProductStatusDto;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {

    default ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        switch (product.getProductType()) {
            case DIGITAL:
                return toDigitalDto((DigitalProduct) product);
            case PHYSICAL:
            default:
                return toPhysicalDto((PhysicalProduct) product);
        }
    }

    PhysicalProductDto toPhysicalDto(PhysicalProduct product);

    DigitalProductDto toDigitalDto(DigitalProduct product);

    MoneyDto toDto(Money money);

    ProductStatusDto toDto(ProductStatus status);

    default JsonNullable<String> map(String value) {
        return JsonNullable.of(value);
    }

    default OffsetDateTime map(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
