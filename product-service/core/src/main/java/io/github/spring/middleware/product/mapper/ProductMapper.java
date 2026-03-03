package io.github.spring.middleware.product.mapper;

import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.dto.ProductCreateRequestDto;
import io.github.spring.middleware.product.dto.ProductPatchRequestDto;
import io.github.spring.middleware.product.dto.ProductUpdateRequestDto;
import io.github.spring.middleware.product.dto.graphql.ProductInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product mapToProduct(ProductCreateRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product mapToProduct(ProductUpdateRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product mapToProduct(ProductPatchRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product mapToProduct(ProductInput request);

    default String map(JsonNullable<String> value) {
        return value != null && value.isPresent() ? value.get() : null;
    }
}
