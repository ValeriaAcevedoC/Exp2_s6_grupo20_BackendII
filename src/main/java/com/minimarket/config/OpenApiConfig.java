package com.minimarket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI minimarketAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Minimarket Plus API")
                        .version("1.0")
                        .description("API REST para la gestión de productos, inventario, ventas, usuarios y carrito del sistema Minimarket Plus.")
                        .contact(new Contact()
                                .name("Grupo 20")
                                .email("grupo20@duoc.cl"))
                        .license(new License()
                                .name("Uso Académico")));
    }

}