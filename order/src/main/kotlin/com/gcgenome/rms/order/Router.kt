package com.gcgenome.rms.order

import com.gcgenome.rms.SecurityContextRepository
import com.gcgenome.rms.data.CancelOrder_
import com.gcgenome.rms.data.Item_
import com.gcgenome.rms.data.Order_
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration("com.gcgenome.rms.order.Router")
class Router (private val handler: Handler) {
    @Bean("com.gcgenome.rms.order.Router.Bean")
    fun route() = router {
        PUT("/api/orders", ::orders)
        GET("/api/orders", ::findOrders)
        PATCH("/api/orders", ::updateOrders)
        DELETE("/api/samples/{sample-id}", ::cancels)
        PATCH("/api/orders/samples/{sampleId}", :: addSample)
    }
    private fun orders(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Order_::class.java))
            .flatMap { handler.insertOrder(it.t1.principal, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order_::class.java) }
    }

    private fun findOrders(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(handler.findOrders(it.principal), Order_::class.java)
            }
    }

    private fun addSample(request: ServerRequest): Mono<ServerResponse> {
        val sampleId = UUID.fromString(request.pathVariable("sampleId"))
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Item_::class.java))
            .flatMap { handler.addSample(it.t1.principal, sampleId, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order_::class.java) }
    }
    private fun updateOrders(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Order_::class.java))
            .flatMap { handler.updateOrder(it.t1.principal, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order_::class.java) }
    }
    private fun cancels(request: ServerRequest): Mono<ServerResponse> {
        val sampleIdString = request.pathVariable("sample-id")
        val sampleId = UUID.fromString(sampleIdString)
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.cancelOrder(sampleId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), CancelOrder_::class.java) }
    }
}