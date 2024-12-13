package com.gcgenome.rms.request

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
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
class RequestRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val requestHandler: RequestHandler
) {
    @Bean("RequestRouter")
    fun route() = router {
        POST("/w-api/result-service/requests/search", ::selectRequests)
        PATCH("/w-api/result-service/requests", :: updateRequests)
    }

    private fun selectRequests(request: ServerRequest): Mono<ServerResponse> {
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(Query::class.java))
            .flatMap { requestHandler.selectRequests(it.t1, it.t2) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(Mono.just(it.data), RequestDTO::class.java) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error: ${e.cause}") }
    }
    private fun updateRequests(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .then(request.bodyToMono(Array<RequestDTO>::class.java))
            .flatMap { requestHandler.updateRequests(it.toList()) }
            .then(ServerResponse.ok().build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error: ${e.cause}") }
    }
}