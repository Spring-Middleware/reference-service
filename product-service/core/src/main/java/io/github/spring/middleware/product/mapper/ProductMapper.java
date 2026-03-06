package io.github.spring.middleware.product.mapper;

import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.dto.*;
import io.github.spring.middleware.product.dto.graphql.ProductInput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product mapToProduct(ProductCreateRequestDto request);

    Product mapToProduct(ProductUpdateRequestDto request);

    Product mapToProduct(ProductPatchRequestDto request);

    Product mapToProduct(ProductInput request);

    Product mapToProduct(ProductCreateItemDto createItemDto);

    Product mapToProduct(ProductReplaceItemDto replaceItemDto);
}
