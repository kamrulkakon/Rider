package com.example.Rider.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String schemeName = "bearerScheme";
        String swaggerHostUrl = getSwaggerHostUrl();

        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList(schemeName)).components(
                        new Components().addSecuritySchemes(schemeName,
                                new SecurityScheme().name(schemeName).type(SecurityScheme.Type.HTTP).bearerFormat("JWT")
                                        .scheme("bearer"))).info(
                        new Info().title("Family Search Camera Project").description("This is admin panel").version("1.0.0")
                                .license(new License().name("Family Search").url("")))
                .servers(List.of(new Server().url(swaggerHostUrl).description("Admin Panel")));
    }

    private String getSwaggerHostUrl() {
        return "http://localhost:8080";
    }
}