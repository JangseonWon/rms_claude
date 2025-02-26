package com.gcgenome.rms.catalog.request

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class RequestRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val requestHandler: RequestHandler
) {
    @Bean("RequestRouter")
    fun route() = router {
        PUT("/w-api/catalog-service/requests", :: saveRequests)
        POST("/w-api/catalog-service/requests/search", ::searchRequests)
    }
    private val logger: Logger = LoggerFactory.getLogger(RequestRouter::class.java)

    private fun saveRequests(request: ServerRequest): Mono<ServerResponse> {
        val isGroup: Boolean? = request.queryParam("is_group").orElse(null)?.toBoolean()
        return authenticationHandler.principal(request)
            .zipWith(request.bodyToMono(Array<RequestDTO>::class.java))
            .flatMap { requestHandler.saveRequest(it.t1.user, it.t2, isGroup).collectList() }
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
    private fun searchRequests(request: ServerRequest): Mono<ServerResponse> {
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(Query::class.java))
            .flatMap { requestHandler.searchRequests(it.t1, it.t2) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(Mono.just(it.data), RequestDTO::class.java) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
}

