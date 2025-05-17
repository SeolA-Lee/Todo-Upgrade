package com.todolist.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.*;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo List API")
                        .description("Leets Zero100 마지막 과제 Todo-Upgrade API 문서입니다.")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(attachBearerAuthScheme());
    }

    private Components attachBearerAuthScheme() {
        return new Components().addSecuritySchemes("Authorization",
                new SecurityScheme()
                        .type(Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(In.HEADER)
                        .name("Authorization")
                        .description("JWT 토큰을 넣어주세요."));
    }
}
