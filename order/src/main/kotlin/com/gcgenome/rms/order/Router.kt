package com.gcgenome.rms.order

import com.gcgenome.rms.SecurityContextRepository
import com.gcgenome.rms.data.Order_
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono


@Configuration("com.gcgenome.rms.order.Router")
class Router (private val handler: Handler) {
    @Bean("com.gcgenome.rms.order.Router.Bean")
    fun route() = router {
        PUT("/api/orders", ::orders)
        GET("/api/orders", ::findOrders)
    }
    private fun orders(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Order_::class.java))
            .flatMap { handler.order(it.t1.principal, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order_::class.java) }
    }

    private fun findOrders(request: ServerRequest): Mono<ServerResponse> {
        return request.principal().cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(handler.findOrders(it.principal), Order_::class.java)
            }
    }
}