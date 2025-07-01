package com.randevu.randevusistemibackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .servers(
                        List.of(
                                new Server()
                                        .url("https://panel-randevusistemi.m0ekxz.easypanel.host")
                                        .description("Production server")
                        )
                )
                .info(new Info()
                        .title("Randevu Sistemi API")
                        .description("Appointment System REST API documentation")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Randevu Sistemi")
                                .url("https://randevusistemi.com")
                                .email("info@randevusistemi.com"))
                        .license(new License()
                                .name("API License")
                                .url("https://randevusistemi.com/license"))
                );
    }
}
