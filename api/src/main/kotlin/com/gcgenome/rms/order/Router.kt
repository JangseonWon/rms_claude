package com.gcgenome.rms.order

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.CancelOrder
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.exceptions.SampleNotFoundException
import com.gcgenome.rms.exceptions.ServiceNotFoundException
import com.gcgenome.rms.exceptions.ServiceSampleTypeNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*

import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono
import java.util.*

@Configuration("com.gcgenome.rms.order.Route")
class Router (private val handler: Handler){
    @Bean("com.gcgenome.rms.order.Route.Bean")
    fun route() = router {
        GET("/api/orders", contentType(MediaType("application", "vnd.api.v1", Charsets.UTF_8)), ::findOrders)
        GET("/api/orders/samples/{sampleId}", ::findOrder)
        GET("/api/orders/samples", contentType(MediaType("application", "vnd.api.v1", Charsets.UTF_8)), :: findSamples)
        POST("/api/orders", contentType(MediaType("application", "vnd.api.v1+json", Charsets.UTF_8)), ::orders)
        PATCH("/api/orders/items/{item-id}", ::updateOrders)
        PATCH("/api/orders/samples/{sampleId}", :: addSample)
        DELETE("/api/orders/samples/{sample-id}", contentType(MediaType("application", "vnd.api.v1", Charsets.UTF_8)), ::cancels)
    }
    private fun orders(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Order::class.java))
            .flatMap { handler.insertOrder(it.t1.principal, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume (ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume (ServiceSampleTypeNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body type error.") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body error: ${e.message}") }
    }


    private fun findOrders(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(handler.findOrders(it.principal), Order::class.java)
            }
    }
    private fun findSamples(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(handler.findSamples(it.principal), Sample::class.java)
            }
    }

    private fun findOrder(request: ServerRequest): Mono<ServerResponse> {
        val sampleId = UUID.fromString(request.pathVariable("sampleId"))
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.findOrder(sampleId, it.principal) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it),Order::class.java) }
            .onErrorResume (SampleNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Reason phrase: ${e.message}") }
    }
    private fun addSample(request: ServerRequest): Mono<ServerResponse> {
        val sampleId = UUID.fromString(request.pathVariable("sampleId"))
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Item::class.java))
            .flatMap { handler.addSample(it.t1.principal, sampleId, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
    }


    private fun updateOrders(request: ServerRequest): Mono<ServerResponse> {
        val itemIdString = request.pathVariable("item-id")
        val itemId = UUID.fromString(itemIdString)
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Item::class.java))
            .flatMap { handler.updateOrder(it.t1.principal, itemId ,it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
    }


    private fun cancels(request: ServerRequest): Mono<ServerResponse> {
        val sampleIdString = request.pathVariable("sample-id")
        val sampleId = UUID.fromString(sampleIdString)
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.cancelOrder(sampleId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), CancelOrder::class.java) }
    }
}