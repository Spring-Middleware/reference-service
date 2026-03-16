package io.github.spring.middleware.product.dto.graphql;

import lombok.Data;

import java.net.URI;

@Data
public class DigitalProductInput extends ProductInput {

    private Long fileSize;
    private String fileFormat;
    private URI downloadUrl;

}
