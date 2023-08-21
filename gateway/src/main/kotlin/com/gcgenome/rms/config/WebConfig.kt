package com.gcgenome.rms.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class WebConfig(@Value("\${server.static}") val static: String) {
    @Bean
    fun client(): WebClient = WebClient.builder().build()
    @Bean
    fun staticResourceRouter(): RouterFunction<ServerResponse> =
        RouterFunctions.resources("/**", FileSystemResource(static))
}