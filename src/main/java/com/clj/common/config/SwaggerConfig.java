package com.clj.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 配置
 * <p>
 * 需在 pom.xml 中添加依赖：
 * <pre>{@code
 * <dependency>
 *     <groupId>org.springdoc</groupId>
 *     <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
 *     <version>2.6.0</version>
 * </dependency>
 * }</pre>
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("现代农业公司管理系统 API 文档")
                        .description("现代农业公司管理系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("Modern Agricultural Company Management System")))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "Bearer",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}
