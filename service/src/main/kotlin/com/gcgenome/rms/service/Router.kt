package com.gcgenome.rms.service

import com.gcgenome.rms.SecurityContextRepository
import com.gcgenome.rms.data.Service_
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration("com.gcgenome.rms.service.Router")
class Router (private val handler: Handler) {
    @Bean("com.gcgenome.rms.service.Router.Bean")
    fun route() = router { GET("/api/services", ::services) }
    private fun services(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(handler.list(it.principal), Service_::class.java)
            }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume { e ->
                e.printStackTrace()
                ServerResponse.badRequest().bodyValue(e)
            }
    }
}