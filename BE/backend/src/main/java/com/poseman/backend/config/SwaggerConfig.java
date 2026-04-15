package com.poseman.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Springdoc OpenAPI 설정 예제.
 * - 본 예시는 'springdoc-openapi-starter-webmvc-ui' 의존성을 이미 build.gradle에 추가했다고
 * 가정합니다.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("무인 편의점 AI 도난 방지 시스템 API")
                        .description("Backend API 문서 (OpenAPI 3.0)")
                        .version("v1.0.0"));
    }
}
