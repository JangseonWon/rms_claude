package com.gcgenome.rms

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.core.jackson.ModelResolver
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun modelResolver(objectMapper: ObjectMapper): ModelResolver {
        return ModelResolver(objectMapper)
    }

    @Bean
    fun publicApi() = GroupedOpenApi.builder().group("사용자 관리 API")
        .addOpenApiCustomizer { openApi ->
            openApi.servers = listOf(Server().url("http://localhost:9395"))
        }
        .pathsToMatch("/**").build()

    @Bean
    fun springShopOpenAPI() =
        OpenAPI().info(Info().title("RMS API - USER").description("RMS 사용자 관리 API").version("v0.0.1"))
}