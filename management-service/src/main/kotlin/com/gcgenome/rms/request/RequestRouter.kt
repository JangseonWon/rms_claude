package com.gcgenome.rms.user

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exception.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class RequestRouter(
    private val requestHandler: RequestHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean("OrderRouter")
    fun route() = router {
        POST("/w-api/management-service/requests/search", :: searchRequests)
        DELETE("/w-api/management-service/requests", :: deleteRequests)
    }
    private fun searchRequests(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkAdmin(request)
            .then(request.bodyToMono(Query::class.java))
            .flatMap { query ->  requestHandler.searchRequests(query) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(Mono.just(it.data), RequestDTO::class.java) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error: ${e.stackTraceToString()}") }
    }

    private fun deleteRequests(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkAdmin(request)
            .flatMap { request.bodyToMono(Array<RequestDTO>::class.java) }
            .flatMap { requestHandler.deleteRequests(it).collectList() }
            .then(ServerResponse.ok().build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
}

