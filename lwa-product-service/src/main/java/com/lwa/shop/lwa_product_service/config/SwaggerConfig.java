package com.lwa.shop.lwa_product_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .version("1.0.0")
                        .description("API documentation for LWA Product Service (with H2 & Eureka)")
                        .contact(new Contact()
                                .name("Muhammad Jafar Shodiq - Senior Java Developer")
                                .email("jafarshodiq0412@gmail.com")
                                .url("https://www.linkedin.com/in/jafar-shodiq-498354194/")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082/product")
                                .description("Local environment")
                ));
    }
}
