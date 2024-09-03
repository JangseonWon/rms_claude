package com.gcgenome.rms.service

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ManagerAuthenticationException
import com.gcgenome.rms.tables.pojos.Extension
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Configuration
class AlisRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val alisHandler: AlisHandler
) {
    @Bean("AlisRouter")
    fun route() = router {
        PUT("/w-api/management-service/alis/services", :: services)
        PUT("/w-api/management-service/alis/sample-types", :: sampleTypes)
        PUT("/w-api/management-service/alis/extensions", :: extensions)
    }

    private fun services(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .then(alisHandler.updateServices())
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error. : ${it}") }
    }
    private fun sampleTypes(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .then(alisHandler.updateSampleTypes())
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error. : ${it}") }
    }
    private fun extensions(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .then(alisHandler.updateExtensions())
            .flatMap {
                ServerResponse.ok()
                    .header("X-Total-Page", it.first.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Flux.fromIterable(it.second), Extension::class.java)
            }
    }
}

