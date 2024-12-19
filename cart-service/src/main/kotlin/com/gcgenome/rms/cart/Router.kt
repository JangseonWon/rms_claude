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
        POST("/w-api/cart-service/search", :: requestSearch)
        GET("/w-api/cart-service/services/{service_id}/samples/{sample_id}", ::cartInfo)
        GET("/w-api/cart-service/organizations", :: organizations)
        PUT("/w-api/cart-service/requests", :: cartToOrder)
        PATCH("/w-api/cart-service/services/{service_id}/samples/{sample_id}", :: updateRequest)
        GET("/w-api/cart-service/sample_types", :: sampleTypes)
        DELETE("/w-api/cart-service/requests", :: deleteCart)
    }

    private fun cartInfo(request: ServerRequest): Mono<ServerResponse> {
        val sampleId = UUID.fromString(request.pathVariable("sample_id"))
        val serviceId = request.pathVariable("service_id")
        return principal(request)
            .flatMap { handler.getCartInfo(sampleId, serviceId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
    private fun requestSearch(request: ServerRequest): Mono<ServerResponse> {
        return principal(request)
            .zipWith(request.bodyToMono(Query::class.java))
            .flatMap { handler.requestSearch(it.t1, it.t2) }
            .flatMap { ServerResponse.ok()
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it.data), RequestDTO::class.java) }
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
    private fun cartToOrder(request: ServerRequest): Mono<ServerResponse> {
        return principal(request)
            .flatMap { request.bodyToMono(Array<Request>::class.java) }
            .flatMap { handler.cartToOrder(it).collectList() }
            .flatMap { ServerResponse.ok().build()}
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    private fun updateRequest(request: ServerRequest): Mono<ServerResponse> {
        val sampleIdPathVar = UUID.fromString(request.pathVariable("sample_id"))
        val serviceIdPathVar = request.pathVariable("service_id")
        return principal(request)
            .flatMap { request.bodyToMono(Request::class.java) }
            .flatMap { handler.updateRequest(
                it.apply {
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
    private fun deleteCart(request: ServerRequest): Mono<ServerResponse> {
        return principal(request)
            .flatMap { request.bodyToMono(Array<Request>::class.java) }
            .flatMap { handler.deleteCart(it).collectList() }
            .then(ServerResponse.ok().build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
    private fun principal(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal().switchIfEmpty(Mono.error(AuthenticationNotFoundException()))
            .cast(UserAuthentication::class.java)
    }
}
