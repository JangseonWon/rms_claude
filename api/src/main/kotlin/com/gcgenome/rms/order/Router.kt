package com.gcgenome.rms.order

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.exceptions.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono


@Configuration("com.gcgenome.rms.order.Route")
class Router (private val handler: Handler){
    @Bean("com.gcgenome.rms.order.Route.Bean")
    fun route() = router {
        DELETE("/api/orders/{orderId}/services/{serviceId}/samples/{sampleId}", accept(MediaType("application","vnd.gcgenome.rms-v1")), ::deleteOrder)
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
