package com.gcgenome.rms.alis

import com.gcgenome.rms.alis.data.AlisQuery
import com.gcgenome.rms.exceptions.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono


@Configuration("com.gcgenome.rms.order.Route")
class Router (
    private val handler: Handler
){
    @Bean("com.gcgenome.rms.order.Route.Bean")
    fun route() = router {
        POST("/api/alis/requests", contentType(MediaType("application", "xml"))) { request ->
            when (request.headers().accept().firstOrNull()) {
                MediaType("application", "vnd.gcgenome.rms.v1+xml") -> alisRequests(request)
                else -> ServerResponse.status(406).build()
            }
        }
    }

    private fun alisRequests(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(AlisQuery::class.java)
            .flatMap { handler.getRequests(it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_XML).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServerWebInputException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("필수 입력값이 누락되었거나 잘못된 형식입니다.")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error: ${e.cause}") }
    }
}
