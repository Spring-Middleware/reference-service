package io.github.spring.middleware.product.entity;

import lombok.Data;

import java.net.URI;

@Data
public class DigitalProductEntity extends BaseProductEntity {

    private Long fileSize;
    private String fileFormat;
    private URI downloadUrl;
}

