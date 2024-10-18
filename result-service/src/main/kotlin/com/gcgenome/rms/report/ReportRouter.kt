package com.gcgenome.rms.request

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.report.ReportHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class ReportRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val handler: ReportHandler,
) {
    @Bean("ReportRouter")
    fun route() = router {
        GET("/w-api/result-service/reports/{report-id}") { request ->
            val acceptTypes = request.headers().accept()
            when {
                MediaType.APPLICATION_PDF in acceptTypes -> downloadFile(request)
                MediaType.IMAGE_JPEG in acceptTypes -> downloadFile(request)
                MediaType.IMAGE_PNG in acceptTypes -> downloadFile(request)
                else -> ServerResponse.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
            }
        }
        POST("/w-api/result-service/reports") { request ->
            when(request.headers().accept().firstOrNull()){
                MediaType("application", "zip") -> downloadFiles(request)
                else -> ServerResponse.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
            }
        }
    }

    private fun downloadFile(request: ServerRequest): Mono<ServerResponse> {
        val reportId = UUID.fromString(request.pathVariable("report-id"))
        return authenticationHandler.principal(request)
            .flatMap { user -> handler.downloadFile(reportId, user) }
            .flatMap {ServerResponse.ok().contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=$reportId.pdf")
                .body(Mono.just(it), ByteArray::class.java) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error code: $e") }
    }
    private fun downloadFiles(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }
}