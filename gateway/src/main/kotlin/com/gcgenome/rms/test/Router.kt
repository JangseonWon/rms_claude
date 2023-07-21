package com.gcgenome.rms.test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class Router() {
    @Bean("com.gcgenome.rms.test.Router")
    fun route() = router {
        GET("/api/test", ::test)
        GET("/", ::test)
    }
    private fun test(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().bodyValue("API TEST SUCCESS")
    }
}
