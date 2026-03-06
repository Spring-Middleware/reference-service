package io.github.spring.middleware.catalog.mapper;

import io.github.spring.middleware.catalog.domain.Product;
import io.github.spring.middleware.catalog.dto.PagedProductResponseDto;
import io.github.spring.middleware.catalog.dto.ProductInputDto;
import io.github.spring.middleware.catalog.dto.ProductSummaryDto;
import io.github.spring.middleware.catalog.dto.ProductWithIdInputDto;
import io.github.spring.middleware.product.dto.ProductCreateItemDto;
import io.github.spring.middleware.product.dto.ProductDto;
import io.github.spring.middleware.product.dto.ProductReplaceItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toDomain(ProductDto productDTO);

    Product toDomain(ProductInputDto productInputDto);

    Product toDomain(ProductWithIdInputDto productWithIdInputDto);

    ProductCreateItemDto toCreateItemDto(Product product);

    ProductReplaceItemDto toReplaceItemDto(Product product);

    @Mapping(target = "items", source = "content")
    @Mapping(target = "page", source = "number")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "totalItems", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    PagedProductResponseDto toPagedResponseDto(Page<Product> product);

    ProductSummaryDto toSummaryDto(Product product);

}
