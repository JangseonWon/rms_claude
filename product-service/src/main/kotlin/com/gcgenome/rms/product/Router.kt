package com.gcgenome.rms.product

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.SampleType
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.service.AuthenticationHandler
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
    private val handler: Handler,
    private val authentication: AuthenticationHandler
){
    @Bean("Router")
    fun route() = router {
        GET("/w-api/product-service/organizations", :: getOrganizations)
        GET("/w-api/product-service/services", :: getServices)
        GET("/w-api/product-service/sample_types", :: getSampleTypes)
        PUT("/w-api/product-service/requests", :: updateRequest)
    }

    private fun getOrganizations(request: ServerRequest): Mono<ServerResponse> {
        return authentication.principal(request)
            .flatMap { handler.getOrganizations(it.user.id!!).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Organization::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    private fun getServices(request: ServerRequest): Mono<ServerResponse> {
        val categoryId = UUID.fromString(request.queryParam("category_id").get())
        return authentication.principal(request)
            .flatMap { handler.getServices(it.user.id!!, categoryId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Organization::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
    private fun getSampleTypes(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.queryParam("service_id").get()
        return authentication.principal(request)
            .flatMap { handler.getSampleTypes(serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), SampleType::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
    private fun updateRequest(request: ServerRequest): Mono<ServerResponse> {
        val orderId:UUID? = request.queryParam("order_id").orElse(null)?.let { UUID.fromString(it) }
        return authentication.principal(request)
            .zipWith(request.bodyToMono(Request::class.java))
            .flatMap { handler.updateRequest(it.t1.user, it.t2, orderId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Request::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}
