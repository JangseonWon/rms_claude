package com.gcgenome.rms

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableAsync
@EnableWebFlux
open class WebConfig(private val objectMapper: ObjectMapper, private val xmlMapper: ObjectMapper) : WebFluxConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*") // any host or put domain(s) here
            .allowedMethods("GET", "OPTIONS") // put the http verbs you want allow
            .allowedHeaders("Authorization", "X-USER-ID", "Content-Type") // put the http headers you want allow
    }
    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
        configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
        configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper, MediaType("application", "vnd.alis-api.v1", Charsets.UTF_8)))
        configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(xmlMapper
            , MediaType("application", "vnd.alis-api.v0", Charsets.UTF_8)
            , MediaType("application", "vnd.alis-api.v0.1", Charsets.UTF_8)
        ))
        configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(xmlMapper
            , MediaType("application", "vnd.alis-api.v0", Charsets.UTF_8)
            , MediaType("application", "vnd.alis-api.v0.1", Charsets.UTF_8)
        ))
    }
}