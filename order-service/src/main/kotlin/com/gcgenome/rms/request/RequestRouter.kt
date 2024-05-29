package com.gcgenome.rms.request

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.exception.*
import org.jooq.exception.DataAccessException
import org.jooq.exception.IntegrityConstraintViolationException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class RequestRouter(
    private val requestHandler: RequestHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean
    fun route() = router {
        POST("/w-api/order-service/requests", ::selectRequests)
        GET("/w-api/order-service/orders/{order_id}/services/{service_id}/samples/{sample_id}", :: selectRequest)
        PATCH("/w-api/order-service/orders/{order_id}/services/{service_id}/samples/{sample_id}", :: updateRequest)
    }

    private fun updateRequest(request: ServerRequest) : Mono<ServerResponse> {
        val order = UUID.fromString(request.pathVariable("order_id"))
        val service = request.pathVariable("service_id")
        val sample = UUID.fromString(request.pathVariable("sample_id"))
        return authenticationHandler.principal(request)
            .flatMap { user ->
                request.bodyToMono(Request::class.java)
                    .flatMap { requestHandler.updateRequestProcess(user.user,
                        it.apply {
                            orderId = order
                            sampleId = sample
                            serviceId = service
                            this.sample!!.id = sampleId
                            this.sample.patientSerial = patient!!.serial
                        }) }
                   .flatMap { requestHandler.checkRequest(order, sample, service) }
                   .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (RequestNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume(IntegrityConstraintViolationException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(OrganizationNotFoundException().message.toString()) }
            .onErrorResume (DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    private fun selectRequest(request: ServerRequest) : Mono<ServerResponse> {
        val orderId = UUID.fromString(request.pathVariable("order_id"))
        val serviceId = request.pathVariable("service_id")
        val sampleId = UUID.fromString(request.pathVariable("sample_id"))
        return authenticationHandler.principal(request)
            .flatMap { requestHandler.checkRequest(orderId, sampleId, serviceId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    private fun selectRequests(request: ServerRequest) : Mono<ServerResponse> {
        var status = request.queryParam("status")
        if (!status.isPresent)
            status = Optional.of("all")
        return authenticationHandler.principal(request)
            .zipWith(request.bodyToMono(Query::class.java))
            .flatMap { requestHandler.selectRequests(it.t1.user, status.get(), it.t2.copy(page= it.t2.page - 1)) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}