package io.github.spring.middleware.product.service;

import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.domain.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product createProduct(Product product);

    Product getProduct(UUID id);

    Product replaceProduct(UUID id, Product product);

    Product patchProduct(UUID id, Product product);

    Page<Product> listProducts(String q, ProductStatus status, Pageable pageable);

    void deleteProduct(UUID id);

    List<Product> lookupProducts(List<UUID> ids);
}

