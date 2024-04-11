package com.gcgenome.rms.order

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.SearchCondition
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
        POST("/api/orders",contentType(MediaType("application","json")).and(accept(MediaType("application","vnd.gcgenome.rms-v1+json"))), :: searchOrders)
        DELETE("/api/orders/{orderId}/services/{serviceId}/samples/{sampleId}", accept(MediaType("application","vnd.gcgenome.rms-v1")), ::deleteOrder)
    }

    private fun searchOrders(request: ServerRequest) :  Mono<ServerResponse> {
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(SearchCondition::class.java))
            .flatMap { handler.searchOrderList(it.t1.user.id,it.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue(ServerInputException().message.toString())}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("${e.message}") }
    }

    private fun deleteOrder(request: ServerRequest): Mono<ServerResponse> {
        val serial = request.pathVariable("orderId")
        val serviceId = request.pathVariable("serviceId")
        val barcode = request.pathVariable("sampleId")
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.cancelOrder(serial, serviceId, barcode) }
            .then (ServerResponse.status(HttpStatus.OK).bodyValue("의뢰 취소 성공: Header Status 200"))
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue("의뢰 취소 불가: Header Status 406, Status Message: ${e.message}") }
            .onErrorResume(SampleNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue("의뢰 취소 불가: Header Status 406, Status Message: ${e.message}")}
            .onErrorResume(SampleDeleteException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue("의뢰 취소 불가: Header Status 406, Status Message: ${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("${e.message}") }
    }
}
