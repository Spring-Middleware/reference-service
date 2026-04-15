package io.github.spring.middleware.review.boot;

import io.github.spring.middleware.annotations.EnableGraphQLLinks;
import io.github.spring.middleware.annotations.EnableMiddlewareClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = {"io.github.spring.middleware.review.repository"})
@EnableMiddlewareClients(basePackages = {"io.github.spring.middleware"})
@EnableGraphQLLinks(basePackages = {"io.github.spring.middleware.review.domain"})
@SpringBootApplication(scanBasePackages = {"io.github.spring.middleware"})
public class ReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewApplication.class, args);
    }
}
