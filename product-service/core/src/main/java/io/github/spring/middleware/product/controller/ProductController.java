package io.github.spring.middleware.product.controller;

import io.github.spring.middleware.annotation.Register;
import io.github.spring.middleware.product.api.ProductsApi;
import io.github.spring.middleware.product.domain.Product;
import io.github.spring.middleware.product.domain.ProductStatus;
import io.github.spring.middleware.product.dto.*;
import io.github.spring.middleware.product.mapper.ProductDtoMapper;
import io.github.spring.middleware.product.mapper.ProductMapper;
import io.github.spring.middleware.product.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@Register(name = "product")
@AllArgsConstructor
public class ProductController implements ProductsApi {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductDtoMapper productDtoMapper;

    @Override
    public ProductDto createProduct(ProductCreateRequestDto productCreateRequestDto) {
        log.info("Received request to create product: {}", productCreateRequestDto);
        Product product = productMapper.mapToProduct(productCreateRequestDto);
        product = productService.createProduct(product);
        return productDtoMapper.toDto(product);
    }

    @Override
    public void deleteProduct(UUID id) {
        log.info("Received request to delete product with id: {}", id);
        productService.deleteProduct(id);
    }

    @Override
    public ProductDto getProduct(UUID id) {
        final Product product = productService.getProduct(id);
        return productDtoMapper.toDto(product);
    }

    @Override
    public PagedProductResponseDto listProducts(String q, ProductStatusDto status, String catalogId, Integer page, Integer size, String sort) {
        log.info("Rest request to list products. q: {}, status: {}, page: {}, size: {}", q, status, page, size);
        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                sort == null || sort.isBlank() ? Sort.unsorted() : Sort.by(sort.split(","))
        );

        ProductStatus domainStatus = status != null ? ProductStatus.valueOf(status.name()) : null;

        Page<Product> productsPage = productService.listProducts(q, domainStatus, pageable);

        PagedProductResponseDto response = new PagedProductResponseDto();
        response.setItems(productsPage.getContent().stream().map(productDtoMapper::toDto).toList());
        response.setPage(productsPage.getNumber());
        response.setSize(productsPage.getSize());
        response.setTotalItems((int) productsPage.getTotalElements());
        response.setTotalPages(productsPage.getTotalPages());

        return response;
    }

    @Override
    public ProductDto patchProduct(UUID id, ProductPatchRequestDto productPatchRequestDto) {
        log.info("Received request to patch product with id: {}", id);
        Product product = productMapper.mapToProduct(productPatchRequestDto);
        final Product patchedProduct = productService.patchProduct(id, product);
        return productDtoMapper.toDto(patchedProduct);
    }

    @Override
    public ProductDto replaceProduct(UUID id, ProductUpdateRequestDto productUpdateRequestDto) {
        log.info("Received request to replace product with id: {}", id);
        Product product = productMapper.mapToProduct(productUpdateRequestDto);
        final Product replacedProduct = productService.replaceProduct(id, product);
        return productDtoMapper.toDto(replacedProduct);
    }
}
