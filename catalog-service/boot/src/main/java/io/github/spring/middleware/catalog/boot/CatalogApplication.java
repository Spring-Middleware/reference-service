package io.github.spring.middleware.catalog.boot;

import io.github.spring.middleware.annotations.EnableMiddlewareClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = {"io.github.spring.middleware.catalog.repository"})
@EnableMiddlewareClients(basePackages = {"io.github.spring.middleware"})
@SpringBootApplication(scanBasePackages = {"io.github.spring.middleware"})
public class CatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
    }

}
