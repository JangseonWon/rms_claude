package com.gcgenome.rms.download

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.authentication.UserAuthentication
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
    fun route() = router {
        GET("/w-api/order-service/orders/{order_id}/services/{service_id}/samples/{sample_id}/form", ::download)
        GET("/w-api/order-service/requests/reports/{report_id}/file", ::downloadRequest)
    }

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

    private fun downloadRequest(request: ServerRequest): Mono<ServerResponse> {
        val requestId = request.pathVariable("report_id")
        return principal(request)
            .flatMap { user -> handler.downloadByRequestId(user, requestId) }
            .flatMap { byteArray -> ServerResponse.ok().contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "attachment; filename=$requestId.pdf")
                    .bodyValue(byteArray) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error code: $e") }
    }


    private fun principal(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal().switchIfEmpty(Mono.error(AuthenticationNotFoundException()))
            .cast(UserAuthentication::class.java)
    }
}