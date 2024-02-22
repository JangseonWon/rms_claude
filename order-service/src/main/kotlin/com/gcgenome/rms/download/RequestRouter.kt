package com.gcgenome.rms.download

import com.gcgenome.rms.config.SecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration("com.gcgenome.rms.service.Router")
class Router (private val handler: RequestHandler) {
    @Bean("com.gcgenome.rms.service-download.Router.Bean")
    fun route() = router { POST("/w-api/order-service/medical-referral/service/{service}/download/{id}", ::download) }

    private fun download(request: ServerRequest): Mono<ServerResponse> {
        val service = request.pathVariable("service")
        val serviceName = handler.capitalizeServiceName(service)
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.handleDownloadByService(it.name, serviceName, request) }
            .flatMap { byteArray -> ServerResponse.ok().contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=test.pdf")
                .bodyValue (byteArray) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
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