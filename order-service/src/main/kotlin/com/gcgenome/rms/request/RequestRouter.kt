package com.gcgenome.rms.request

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.OrderNotFoundException
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
import java.util.*

@Configuration
class RequestRouter(
    val authenticationHandler: AuthenticationHandler,
    val requestHandler: RequestHandler
) {
    @Bean("RequestRouter")
    fun route() = router {
        POST("/w-api/order-service/requests/search", ::selectRequests)
        PATCH("/w-api/order-service/requests",:: updateRequests)
        GET("/w-api/order-service/services/{service_id}/samples/{sample_id}", ::orderInfo)
    }
    private val logger: Logger = LoggerFactory.getLogger(RequestRouter::class.java)
    private fun selectRequests(request: ServerRequest) : Mono<ServerResponse> {
        return authenticationHandler.principal(request).zipWith(request.bodyToMono(Query::class.java))
            .flatMap { requestHandler.selectRequests(it.t1, it.t2) }
            .flatMap { ServerResponse.ok()
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it.data), RequestDTO::class.java ) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }

    private fun updateRequests(request: ServerRequest) : Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .then (request.bodyToMono(Array<RequestDTO>::class.java))
            .flatMap { requests -> requestHandler.updateRequests(requests.toList()) }
            .then(ServerResponse.ok().build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }

    private fun orderInfo(request: ServerRequest): Mono<ServerResponse> {
        val sampleId = UUID.fromString(request.pathVariable("sample_id"))
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.principal(request)
            .flatMap { requestHandler.getOrderInfo(sampleId, serviceId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), RequestDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
}