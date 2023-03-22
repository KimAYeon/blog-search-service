package com.zetn333.blogsearchservice.api.common.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API 명세를 위한 Swagger springdoc-ui 설정
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Blog Search Service API Document")
                .version("v0.1")
                .description("블로그 검색 서비스 프로젝트의 API 명세서입니다.");

          return new OpenAPI()
            .info(info);
    }

}
