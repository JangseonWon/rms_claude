package com.gcgenome.rms.resample

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exceptions.RequestNotFoundException
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
        PUT("/w-api/order-service/orders/{orderId}/services/{serviceId}/samples/{sampleId}", ::addSample)
    }

    private fun addSample(request: ServerRequest): Mono<ServerResponse> {
        val orderId = UUID.fromString(request.pathVariable("orderId"))
        val serviceId = request.pathVariable("serviceId")
        val sampleId = UUID.fromString(request.pathVariable("sampleId"))
        return authenticationHandler.principal(request)
            .zipWith(request.bodyToMono(Request::class.java))
            .flatMap { handler.insertSampleRequest(it.t1.user.id!!, serviceId, orderId, sampleId, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (RequestNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body type error.") }
            .onErrorResume { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body error.") }
    }
}
