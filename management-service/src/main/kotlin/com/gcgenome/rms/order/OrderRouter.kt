package com.gcgenome.rms.user

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exception.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class OrderRouter(
    private val orderHandler: OrderHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean("OrderRouter")
    fun route() = router {
        DELETE("/w-api/management-service/requests", :: deleteRequest)
    }
    private fun deleteRequest(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkAdmin(request)
            .flatMap { request.bodyToMono(Array<RequestDTO>::class.java) }
            .flatMap { orderHandler.deleteOrder(it).collectList() }
            .then(ServerResponse.ok().build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
}

