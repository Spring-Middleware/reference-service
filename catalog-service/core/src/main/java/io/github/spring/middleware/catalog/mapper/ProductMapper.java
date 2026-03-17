package io.github.spring.middleware.catalog.mapper;

import io.github.spring.middleware.catalog.domain.DigitalProduct;
import io.github.spring.middleware.catalog.domain.PhysicalProduct;
import io.github.spring.middleware.catalog.domain.Product;
import io.github.spring.middleware.catalog.dto.DigitalProductInputDto;
import io.github.spring.middleware.catalog.dto.DigitalProductWithIdInputDto;
import io.github.spring.middleware.catalog.dto.PagedProductResponseDto;
import io.github.spring.middleware.catalog.dto.PhysicalProductInputDto;
import io.github.spring.middleware.catalog.dto.PhysicalProductWithIdInputDto;
import io.github.spring.middleware.catalog.dto.ProductInputDto;
import io.github.spring.middleware.catalog.dto.ProductSummaryDto;
import io.github.spring.middleware.catalog.dto.ProductWithIdInputDto;
import io.github.spring.middleware.product.dto.DigitalProductCreateItemDto;
import io.github.spring.middleware.product.dto.DigitalProductDto;
import io.github.spring.middleware.product.dto.DigitalProductReplaceItemDto;
import io.github.spring.middleware.product.dto.PhysicalProductCreateItemDto;
import io.github.spring.middleware.product.dto.PhysicalProductDto;
import io.github.spring.middleware.product.dto.PhysicalProductReplaceItemDto;
import io.github.spring.middleware.product.dto.ProductCreateItemDto;
import io.github.spring.middleware.product.dto.ProductDto;
import io.github.spring.middleware.product.dto.ProductReplaceItemDto;
import io.github.spring.middleware.product.dto.ProductTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    default Product toDomain(ProductDto productDTO) {
        if (productDTO == null) {
            return null;
        }

        ProductTypeDto productTypeDto = productDTO.getProductType();
        if (productTypeDto == ProductTypeDto.DIGITAL) {
            return toDomain((DigitalProductDto) productDTO);
        } else if (productTypeDto == ProductTypeDto.PHYSICAL) {
            return toDomain((PhysicalProductDto) productDTO);
        } else {
            throw new IllegalArgumentException(STR."Unknown product type: \{productTypeDto}");
        }
    }

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "offsetDateTimeToInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToInstant")
    DigitalProduct toDomain(DigitalProductDto digitalProductDTO);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "offsetDateTimeToInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToInstant")
    PhysicalProduct toDomain(PhysicalProductDto physicalProductDTO);


    default Product toDomain(ProductInputDto productInputDto) {
        if (productInputDto == null) {
            return null;
        }

        io.github.spring.middleware.catalog.dto.ProductTypeDto productTypeDto = productInputDto.getProductType();
        if (productTypeDto == io.github.spring.middleware.catalog.dto.ProductTypeDto.DIGITAL) {
            return toDomain((DigitalProductInputDto) productInputDto);
        } else if (productTypeDto == io.github.spring.middleware.catalog.dto.ProductTypeDto.PHYSICAL) {
            return toDomain((PhysicalProductInputDto) productInputDto);
        } else {
            throw new IllegalArgumentException(STR."Unknown product type: \{productTypeDto}");
        }
    }

    DigitalProduct toDomain(DigitalProductInputDto digitalProductInputDTO);

    PhysicalProduct toDomain(PhysicalProductInputDto physicalProductInputDTO);


    default Product toDomain(ProductWithIdInputDto productWithIdInputDto) {
        if (productWithIdInputDto == null) {
            return null;
        }

        io.github.spring.middleware.catalog.dto.ProductTypeDto productTypeDto = productWithIdInputDto.getProductType();
        if (productTypeDto == io.github.spring.middleware.catalog.dto.ProductTypeDto.DIGITAL) {
            return toDomain((DigitalProductWithIdInputDto) productWithIdInputDto);
        } else if (productTypeDto == io.github.spring.middleware.catalog.dto.ProductTypeDto.PHYSICAL) {
            return toDomain((PhysicalProductWithIdInputDto) productWithIdInputDto);
        } else {
            throw new IllegalArgumentException(STR."Unknown product type: \{productTypeDto}");
        }
    }

    DigitalProduct toDomain(DigitalProductWithIdInputDto digitalProductWithIdInputDTO);

    PhysicalProduct toDomain(PhysicalProductWithIdInputDto physicalProductWithIdInputDTO);

    default ProductCreateItemDto toCreateItemDto(Product product) {
        if (product == null) {
            return null;
        }

        if (product instanceof DigitalProduct) {
            return toCreateItemDto((DigitalProduct) product);
        } else if (product instanceof PhysicalProduct) {
            return toCreateItemDto((PhysicalProduct) product);
        } else {
            throw new IllegalArgumentException(STR."Unknown product type: \{product.getClass().getName()}");
        }
    }

    DigitalProductCreateItemDto toCreateItemDto(DigitalProduct product);

    PhysicalProductCreateItemDto toCreateItemDto(PhysicalProduct product);

    default ProductReplaceItemDto toReplaceItemDto(Product product) {
        if (product == null) {
            return null;
        }

        if (product instanceof DigitalProduct) {
            return toReplaceItemDto((DigitalProduct) product);
        } else if (product instanceof PhysicalProduct) {
            return toReplaceItemDto((PhysicalProduct) product);
        } else {
            throw new IllegalArgumentException(STR."Unknown product type: \{product.getClass().getName()}");
        }
    }

    DigitalProductReplaceItemDto toReplaceItemDto(DigitalProduct product);

    PhysicalProductReplaceItemDto toReplaceItemDto(PhysicalProduct product);

    @Mapping(target = "items", source = "content")
    @Mapping(target = "page", source = "number")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "totalItems", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    PagedProductResponseDto toPagedResponseDto(Page<Product> product);

    default ProductWithIdInputDto toWithIdInputDto(Product product) {
        if (product == null) {
            return null;
        }

        if (product instanceof DigitalProduct) {
            return toWithIdInputDto((DigitalProduct) product);
        } else if (product instanceof PhysicalProduct) {
            return toWithIdInputDto((PhysicalProduct) product);
        } else {
            throw new IllegalArgumentException(STR."Unknown product type: \{product.getClass().getName()}");
        }
    }

    DigitalProductWithIdInputDto toWithIdInputDto(DigitalProduct product);

    PhysicalProductWithIdInputDto toWithIdInputDto(PhysicalProduct product);

    ProductSummaryDto toSummaryDto(Product product);

    @Named("offsetDateTimeToInstant")
    default java.time.Instant offsetDateTimeToInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toInstant() : null;
    }

}
