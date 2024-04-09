package com.gcgenome.rms.resample

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.OrderNotFoundException
import com.gcgenome.rms.exception.RequestForbiddenException
import com.gcgenome.rms.exception.SampleDeleteException
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
class ResampleRouter (
    private val handler: ResampleHandler,
    private val authenticationHandler: AuthenticationHandler
){
    @Bean("ResampleServiceRouter")
    fun route() = router {
        GET("/w-api/order-service/orders/{order_id}", ::traceSample)
        PUT("/w-api/order-service/orders/{order_id}/services/{service_id}/samples/{sample_id}", ::addSample)
        DELETE("/w-api/order-service/orders/{order_id}/services/{service_id}/samples/{sample_id}", ::cancelRequest)
    }

    private fun traceSample(request: ServerRequest): Mono<ServerResponse> {
        val orderId = UUID.fromString(request.pathVariable("order_id"))
        return authenticationHandler.principal(request)
            .flatMap { handler.traceSample(orderId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body type error.") }
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("server error: ${e}") }
    }

    private fun addSample(request: ServerRequest): Mono<ServerResponse> {
        val (order, service, sample) = urlAssignment(request)
        return authenticationHandler.principal(request)
            .zipWith(request.bodyToMono(Request::class.java))
            .flatMap { handler.insertSampleRequest(it.t1.user, it.t2.apply { orderId = order; sampleId = sample; serviceId = service}) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body type error.") }
            .onErrorResume { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body error.") }
    }

    private fun cancelRequest(request: ServerRequest): Mono<ServerResponse> {
        val (orderId, serviceId, sampleId) = urlAssignment(request)
        return authenticationHandler.principal(request)
            .flatMap { handler.cancelRequest(it.user, orderId, serviceId, sampleId) }
            .then (ServerResponse.status(HttpStatus.OK).bodyValue("의뢰 취소 성공"))
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("의뢰 취소 불가, Message: ${e.message}") }
            .onErrorResume(RequestForbiddenException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("의뢰 취소 불가, Message: ${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("${e.message}") }
    }

    private fun urlAssignment(request: ServerRequest): Triple<UUID, String, UUID> {
        val orderId = UUID.fromString(request.pathVariable("order_id"))
        val serviceId = request.pathVariable("service_id")
        val sampleId = UUID.fromString(request.pathVariable("sample_id"))
        return Triple(orderId, serviceId, sampleId)
    }
}
