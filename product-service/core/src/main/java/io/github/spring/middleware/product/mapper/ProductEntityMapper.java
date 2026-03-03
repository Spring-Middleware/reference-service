package io.github.spring.middleware.product.mapper;

import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    ProductEntity toEntity(Product product);

    Product toDomain(ProductEntity entity);
}

