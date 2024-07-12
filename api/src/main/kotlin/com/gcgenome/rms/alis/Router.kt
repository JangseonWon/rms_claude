package com.gcgenome.rms.alis

import com.gcgenome.rms.data.AlisQuery
import com.gcgenome.rms.exceptions.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("입력 정보가 잘못되었습니다.")}
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
    }
}
