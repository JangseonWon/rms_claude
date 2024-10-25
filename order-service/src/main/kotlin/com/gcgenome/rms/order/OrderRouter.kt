package com.gcgenome.rms.order

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ColumnNotFoundException
import org.jooq.exception.DataAccessException
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
class OrderRouter(
    private val authenticationHandler: AuthenticationHandler,
    private val orderHandler: OrderHandler
) {
    @Bean("OrderRouter")
    fun route() = router {
        POST("/w-api/order-service/search", ::selectRequestsStatusOrder)
        GET("/w-api/order-service/services/{service_id}/samples/{sample_id}", :: selectRequest)
    }

    private fun selectRequestsStatusOrder(request: ServerRequest) : Mono<ServerResponse> {
        return authenticationHandler.principal(request).zipWith(request.bodyToMono(Query::class.java))
            .flatMap { orderHandler.selectRequestsStatusOrder(it.t1, it.t2) }
            .flatMap { ServerResponse.ok()
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it.data), RequestDTO::class.java ) }
            .onErrorResume(DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    private fun selectRequest(request: ServerRequest) : Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        val sampleId = UUID.fromString(request.pathVariable("sample_id"))
        return authenticationHandler.principal(request)
            .flatMap { orderHandler.checkRequest(sampleId, serviceId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}