package com.gcgenome.rms.download

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration("com.gcgenome.rms.service.Router")
class Router (
    private val handler: DownloadHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean("com.gcgenome.rms.service-download.Router.Bean")
    fun route() = router { GET("/w-api/order-service/orders/{order_id}/services/{service_id}/samples/{sample_id}/form", ::download) }

    private fun download(request: ServerRequest): Mono<ServerResponse> {
        val orderId = request.pathVariable("order_id")
        val serviceId = request.pathVariable("service_id")
        val sampleId = request.pathVariable("sample_id")
        val type = request.queryParam("type")
        return  authenticationHandler.principal(request)
            .flatMap { handler.handleDownloadByService(it.user.name!!, serviceId, UUID.fromString(sampleId)) }
            .flatMap { byteArray -> ServerResponse.ok().contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=test.pdf")
                .bodyValue (byteArray) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e ->
                e.printStackTrace()
                ServerResponse.badRequest().bodyValue(e)
            }
    }
}

fun interface HandlerAdapter : (ServerRequest) -> Mono<ServerResponse>

fun ((ServerRequest) -> Mono<ServerResponse>).toHandlerFunction(): HandlerAdapter {
    return HandlerAdapter { request ->
        this.invoke(request)
    }
}