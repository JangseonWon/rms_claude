package com.gcgenome.rms.cart

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import com.gcgenome.rms.exceptions.OrderNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class Router (
    private val handler: Handler
){
    @Bean("CartServiceRouter")
    fun route() = router {
        GET("/w-api/cart-service/orders/{order_id}/services/{service_id}/samples/{sample_id}", ::cartInfo)
        GET("/w-api/cart-service/organizations", :: organizations)
        POST("/w-api/cart-service/requests", :: requests)
        PATCH("/w-api/cart-service/orders/{order_id}/services/{service_id}/samples/{sample_id}", :: updateRequest)
        GET("/w-api/cart-service/sample_types", :: sampleTypes)

    }

    private fun cartInfo(request: ServerRequest): Mono<ServerResponse> {
        val orderId = UUID.fromString(request.pathVariable("order_id"))
        val sampleId = UUID.fromString(request.pathVariable("sample_id"))
        val serviceId = request.pathVariable("service_id")
        return principal(request)
            .flatMap { handler.getCartInfo(orderId, sampleId, serviceId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
    private fun requests(request: ServerRequest): Mono<ServerResponse> {
        return principal(request)
            .zipWith(request.bodyToMono(Query::class.java))
            .flatMap { handler.requests(it.t1.user, it.t2) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Page", it.second.totalPage.toString())
                .body(Mono.just(it.first), Request::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
    private fun organizations(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.queryParam("user_id").get()
        return principal(request)
            .flatMap { handler.organizations(userId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Organization::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
    private fun updateRequest(request: ServerRequest): Mono<ServerResponse> {
        val orderIdPathVar = UUID.fromString(request.pathVariable("order_id"))
        val sampleIdPathVar = UUID.fromString(request.pathVariable("sample_id"))
        val serviceIdPathVar = request.pathVariable("service_id")
        return principal(request)
            .flatMap { request.bodyToMono(Request::class.java) }
            .flatMap { handler.updateRequest(
                it.apply {
                    orderId = orderIdPathVar
                    sampleId = sampleIdPathVar
                    serviceId = serviceIdPathVar
                }
            ) }
            .flatMap { ServerResponse.ok().bodyValue(it)}
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
    private fun sampleTypes(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.queryParam("service_id").get()
        return principal(request)
            .flatMap { handler.sampleTypes(serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), SampleType::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
    private fun principal(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal().switchIfEmpty(Mono.error(AuthenticationNotFoundException()))
            .cast(UserAuthentication::class.java)
    }
}
