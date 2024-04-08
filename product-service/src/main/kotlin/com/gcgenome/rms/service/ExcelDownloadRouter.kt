package com.gcgenome.rms.service

import com.fasterxml.jackson.databind.JsonNode
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class ExcelDownloadRouter(
    private val handler: ExcelDownloadHandler,
    private val authentication: AuthenticationHandler
) {
    @Bean("ExcelDownloadServiceRouter")
    fun route() = router {
        POST("/w-api/product-service/excel", ::excel)
    }

    private fun excel(request: ServerRequest): Mono<ServerResponse> {
        return authentication.principal(request)
            .flatMap { userAuth ->
                val errorText = "값이 빈 에러"
                request.bodyToMono(JsonNode::class.java)
                    .flatMap { jsonNode -> handler.generateExcelDate().zipWith(handler.generateExcelFile(jsonNode)) }
                    .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header("Content-Disposition", "attachment; filename=\"${userAuth.user.id}_${it.t1}_order.xlsx\"")
                        .bodyValue(it.t2) }
                    .switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(errorText))
                    .onErrorResume(IllegalArgumentException::class.java) {
                        ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(errorText)
                    }
            }.onErrorResume (AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
    }
}
