package io.github.spring.middleware.product.boot;

import io.github.spring.middleware.annotations.EnableMiddlewareClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = {"io.github.spring.middleware.product.repository"})
@EnableMiddlewareClients(basePackages = {"io.github.spring.middleware"})
@SpringBootApplication(scanBasePackages = {"io.github.spring.middleware"})
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
