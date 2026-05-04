package io.github.spring.middleware.catalog.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@ConfigurationPropertiesScan(basePackages = "io.github.spring.middleware")
@SpringBootApplication(scanBasePackages = {"io.github.spring.middleware"})
public class CatalogChatBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogChatBootApplication.class, args);

    }

}
