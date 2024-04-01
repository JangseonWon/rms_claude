package com.gcgenome.rms.request

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
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
import java.util.UUID

@Configuration
class RequestRouter(
    private val requestHandler: RequestHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean
    fun route() = router {
        POST("/w-api/order-service/orders", ::selectRequests)
        GET("/w-api/order-service/orders/{order_id}/services/{service_id}/samples/{sample_id}", :: selectRequest)
    }

    private fun selectRequest(request: ServerRequest) : Mono<ServerResponse> {
        val orderId = UUID.fromString(request.pathVariable("order_id"))
        val serviceId = request.pathVariable("service_id")
        val sampleId = UUID.fromString(request.pathVariable("sample_id"))
        return authenticationHandler.principal(request)
            .flatMap { requestHandler.selectRequest(orderId, sampleId, serviceId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    private fun selectRequests(request: ServerRequest) : Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .zipWith(request.bodyToMono(Query::class.java))
            .flatMap { requestHandler.selectRequests(it.t1.user.id!!, it.t2.copy(page= it.t2.page - 1)) }
            .flatMap { request ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(request)
            }.onErrorResume(DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}