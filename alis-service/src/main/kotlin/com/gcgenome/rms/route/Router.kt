package com.gcgenome.rms.route

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.data.Service
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import com.gcgenome.rms.service.ServiceHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class Router (
    private val serviceHandler: ServiceHandler
) {
    @Bean
    fun route() = router {
        PATCH("/w-api/alis-service/services", ::syncServices)
    }

    private fun syncServices(request: ServerRequest) : Mono<ServerResponse> {
        return principal(request)
            .flatMap { serviceHandler.syncServices().collectList() }
            .flatMap { service -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(service) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    private fun principal(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal().switchIfEmpty(Mono.error(AuthenticationNotFoundException()))
            .cast(UserAuthentication::class.java)
    }
}

