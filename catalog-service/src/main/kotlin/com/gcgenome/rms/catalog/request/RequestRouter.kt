package com.gcgenome.rms.catalog.request

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class RequestRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val requestHandler: RequestHandler
) {
    @Bean("RequestRouter")
    fun route() = router {
        PUT("/w-api/catalog-service/requests", :: saveRequests)
    }
    private fun saveRequests(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .zipWith(request.bodyToMono(Array<RequestDTO>::class.java))
            .flatMap { requestHandler.saveRequest(it.t1.user, it.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}

