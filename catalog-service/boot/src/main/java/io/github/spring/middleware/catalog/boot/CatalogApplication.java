package io.github.spring.middleware.catalog.boot;

import io.github.spring.middleware.annotations.EnableMiddlewareClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@EnableMongoRepositories(basePackages = {"io.github.spring.middleware.catalog.repository"})
@EnableMiddlewareClients(basePackages = {"io.github.spring.middleware"})
@SpringBootApplication(scanBasePackages = {"io.github.spring.middleware"})
public class CatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);

    }

    @Bean
    ApplicationRunner runner(ApplicationContext ctx) {
        return args -> {
            log.info(STR."UserDetailsService beans -> \{ctx.getBeansOfType(UserDetailsService.class)}");
            log.info(STR."PasswordEncoder beans -> \{ctx.getBeansOfType(PasswordEncoder.class)}");
            log.info(STR."AuthenticationProvider beans -> \{ctx.getBeansOfType(AuthenticationProvider.class)}");
        };
    }

}
