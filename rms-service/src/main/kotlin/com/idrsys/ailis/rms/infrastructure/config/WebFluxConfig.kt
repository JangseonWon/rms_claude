package com.idrsys.ailis.rms.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

/**
 * WebFlux 설정
 *
 * - CORS 설정
 * - 기타 웹 관련 설정
 */
@Configuration
@EnableWebFlux
class WebFluxConfig : WebFluxConfigurer {

    /**
     * CORS 설정
     *
     * 개발 환경: 모든 출처 허용
     * 운영 환경: 특정 도메인만 허용하도록 수정 필요
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
