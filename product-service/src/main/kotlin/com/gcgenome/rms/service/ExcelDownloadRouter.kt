package com.gcgenome.rms.service

import com.fasterxml.jackson.databind.JsonNode
import com.gcgenome.rms.config.SecurityContextRepository
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
    private val handler: ExcelDownloadHandler
) {
    @Bean("ExcelDownloadServiceRouter")
    fun route() = router {
        POST("/w-api/product-service/excel", ::excel)
    }

    private fun excel(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { userAuth ->
                val errorText = "값이 빈 에러"
                request.bodyToMono(JsonNode::class.java)
                    .flatMap { jsonNode -> handler.generateExcelDate().zipWith(handler.generateExcelFile(jsonNode)) }
                    .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header("Content-Disposition", "attachment; filename=\"${userAuth.name}_${it.t1}_order.xlsx\"")
                        .bodyValue(it.t2) }
                    .switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(errorText))
                    .onErrorResume(IllegalArgumentException::class.java) {
                        ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(errorText)
                    }
            }
    }
}
