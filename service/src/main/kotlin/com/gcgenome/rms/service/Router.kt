package com.gcgenome.rms.service

import com.gcgenome.rms.authentication.UserAuthentication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class Router {
    @Bean
    fun route() = router {
        GET("/w-api/jwt/test", ::test)
    }

    private fun test(request: ServerRequest): Mono<ServerResponse> {
        return request.principal()
            .cast(UserAuthentication::class.java)
            .flatMap { ServerResponse.ok().body(Mono.just(it), UserAuthentication::class.java) }
    }
}