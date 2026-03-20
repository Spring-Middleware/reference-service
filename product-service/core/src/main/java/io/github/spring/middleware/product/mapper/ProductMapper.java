package io.github.spring.middleware.product.mapper;

import io.github.spring.middleware.product.domain.DigitalProduct;
import io.github.spring.middleware.product.domain.Money;
import io.github.spring.middleware.product.domain.PhysicalProduct;
import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.domain.ProductType;
import io.github.spring.middleware.product.dto.*;
import io.github.spring.middleware.product.dto.graphql.ProductInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    default Product mapToProduct(ProductCreateRequestDto request) {
        if (request == null) {
            return null;
        }
        ProductTypeDto type = request.getProductType();
        if (type == ProductTypeDto.DIGITAL) {
            return mapToDigitalDomain((DigitalProductCreateRequestDto) request);
        }
        return mapToPhysicalDomain((PhysicalProductCreateRequestDto) request);
    }

    default Product mapToProduct(ProductUpdateRequestDto request) {
        if (request == null) {
            return null;
        }
        ProductTypeDto type = request.getProductType();
        if (type == ProductTypeDto.DIGITAL) {
            return mapToDigitalDomain((DigitalProductUpdateRequestDto) request);
        }
        return mapToPhysicalDomain((PhysicalProductUpdateRequestDto) request);
    }

    default Product mapToProduct(ProductPatchRequestDto request) {
        if (request == null) {
            return null;
        }
        ProductTypeDto type = request.getProductType();
        if (type == ProductTypeDto.DIGITAL) {
            return mapToDigitalDomain((DigitalProductPatchRequestDto) request);
        }
        return mapToPhysicalDomain((PhysicalProductPatchRequestDto) request);
    }

    default Product mapToProduct(ProductCreateItemDto createItemDto) {
        if (createItemDto == null) {
            return null;
        }
        ProductTypeDto type = createItemDto.getProductType();
        if (type == ProductTypeDto.DIGITAL) {
            return mapToDigitalDomain((DigitalProductCreateItemDto) createItemDto);
        }
        return mapToPhysicalDomain((PhysicalProductCreateItemDto) createItemDto);
    }

    default Product mapToProduct(ProductReplaceItemDto replaceItemDto) {
        if (replaceItemDto == null) {
            return null;
        }
        ProductTypeDto type = replaceItemDto.getProductType();
        if (type == ProductTypeDto.DIGITAL) {
            return mapToDigitalDomain((DigitalProductReplaceItemDto) replaceItemDto);
        }
        return mapToPhysicalDomain((PhysicalProductReplaceItemDto) replaceItemDto);
    }

    // GraphQL: elección de subtipo según productType
    default Product mapToProduct(ProductInput input) {
        if (input == null) {
            return null;
        }
        ProductType type = input.getProductType();
        if (type == null || type == ProductType.PHYSICAL) {
            return mapToPhysicalDomain(input);
        }
        if (type == ProductType.DIGITAL) {
            return mapToDigitalDomain(input);
        }
        return mapToPhysicalDomain(input);
    }

    PhysicalProduct mapToPhysicalDomain(PhysicalProductCreateRequestDto request);

    PhysicalProduct mapToPhysicalDomain(PhysicalProductUpdateRequestDto request);

    PhysicalProduct mapToPhysicalDomain(PhysicalProductPatchRequestDto request);

    PhysicalProduct mapToPhysicalDomain(PhysicalProductCreateItemDto createItemDto);

    PhysicalProduct mapToPhysicalDomain(PhysicalProductReplaceItemDto replaceItemDto);

    PhysicalProduct mapToPhysicalDomain(ProductInput input);

    DigitalProduct mapToDigitalDomain(DigitalProductCreateRequestDto request);

    DigitalProduct mapToDigitalDomain(DigitalProductUpdateRequestDto request);

    DigitalProduct mapToDigitalDomain(DigitalProductPatchRequestDto request);

    DigitalProduct mapToDigitalDomain(DigitalProductCreateItemDto createItemDto);

    DigitalProduct mapToDigitalDomain(DigitalProductReplaceItemDto replaceItemDto);

    DigitalProduct mapToDigitalDomain(ProductInput input);

    @Mapping(target = "amount", source = "amount", qualifiedByName = "normalizeMoneyAmount")
    Money toDomain(MoneyDto moneyDto);

    @Named("normalizeMoneyAmount")
    default BigDecimal normalizeMoneyAmount(BigDecimal amount) {
        return amount == null ? null : amount.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
    }
}
