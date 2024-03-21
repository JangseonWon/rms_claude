package com.gcgenome.rms.order

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.data.SampleSearchCondition
import com.gcgenome.rms.exceptions.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono


@Configuration("com.gcgenome.rms.order.Route")
class Router (private val handler: Handler){
    @Bean("com.gcgenome.rms.order.Route.Bean")
    fun route() = router {
        GET("/api/request-service/samples/{sampleId}", accept(MediaType("application", "vnd.gcgenome.rms-v1")), ::findOrder)
        POST("/api/request-service/samples",contentType(MediaType("application","json")).and(accept(MediaType("application","vnd.gcgenome.rms-v1+json"))), :: searchOrders)
        DELETE("/api/request-service/samples/{sampleId}", accept(MediaType("application","vnd.gcgenome.rms-v1")), ::deleteSample)
    }

    private fun findOrder(request: ServerRequest): Mono<ServerResponse> {
        val barcode = request.pathVariable("sampleId")
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.findOrder(barcode, it.principal) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it),Sample::class.java) }
            .onErrorResume (SampleNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
    }

    private fun searchOrders(request: ServerRequest) :  Mono<ServerResponse> {
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(SampleSearchCondition::class.java))
            .flatMap { handler.searchOrderList(it.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it),Sample::class.java) }
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue(ServerInputException().message.toString())}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Failed to search: ${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }

    }

    private fun deleteSample(request: ServerRequest): Mono<ServerResponse> {
        val barcode = request.pathVariable("sampleId")
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.cancelOrder(barcode) }
            .then (ServerResponse.status(HttpStatus.OK).bodyValue("의뢰 취소 성공: Header Status 200"))
            .onErrorResume(SampleDeleteException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue("의뢰 취소 불가: Header Status 406, Status Message: ${e.message}") }
            .onErrorResume(SampleNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue("의뢰 취소 불가: Header Status 406, Status Message: ${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("의뢰 취소 불가: Header Status 406, Status Message: ${e.message}") }
    }
}