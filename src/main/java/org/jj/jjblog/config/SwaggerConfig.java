package org.jj.jjblog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@OpenAPIDefinition(info=@Info(title="JJ Blog", version = "v1"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi chatOpenApi() {
        String[] paths = {"/api/**"};
        return GroupedOpenApi.builder()
                .group("Jc Open Api v1")
                .pathsToMatch(paths)
                .build();
    }


    // Authorization 보안 헤더를 설정, addList의 name과 addSecuritySchemes의 key 이름이 같으면 헤더에는 Authorization으로 들어감
    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityItem = new SecurityRequirement().addList("Basic Authorization");
        return new OpenAPI().info(new io.swagger.v3.oas.models.info.Info()
                        .title("Boot api01 Project Swagger")
                        .description("Swagger API 설정 프로젝트")
                        .version("v1"))
                .addSecurityItem(securityItem)
                .components(new Components()
                        .addSecuritySchemes("Basic Authorization",
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}