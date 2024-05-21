package com.gcgenome.rms.service

import com.gcgenome.rms.data.Order
import com.gcgenome.rms.exception.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@Configuration
class OrderRouter (
    private val handler: OrderHandler,
    private val authentication: AuthenticationHandler
){
    @Bean("OrderServiceRouter")
    fun route() = router {
        PUT("/w-api/product-service/orders", ::orders)
    }

    private fun orders(request: ServerRequest): Mono<ServerResponse> {
        return authentication.principal(request)
            .zipWith(request.bodyToMono(object : ParameterizedTypeReference<List<Order>>() {}))
            .flatMap { handler.insertOrderProcess(it.t1.user.id!!, it.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume (AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume (ServiceSampleTypeNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume (OrganizationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume (ExtensionIdNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body type error.") }
            .onErrorResume { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body error.") }
    }
}
