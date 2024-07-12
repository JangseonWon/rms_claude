package com.gcgenome.rms.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableAsync
@EnableWebFlux
class WebConfig(private val xmlMapper: ObjectMapper) : WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(xmlMapper, MediaType("application", "xml")))
        configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(xmlMapper, MediaType("application", "xml")))
    }
}