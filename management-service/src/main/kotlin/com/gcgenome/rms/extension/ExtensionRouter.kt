package com.gcgenome.rms.extension

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.ExtensionDTO
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.AdminAuthenticationException
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ExtensionNotFoundException
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
class ExtensionRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val extensionHandler: ExtensionHandler
) {
    @Bean("ExtensionRouter")
    fun route() = router {
        GET("/w-api/management-service/extensions", ::getAllExtensions)
        POST("/w-api/management-service/extensions/search", ::searchExtensions)
        GET("/w-api/management-service/extensions/{extension-id}", ::getExtensionById)
        PATCH("/w-api/management-service/extensions/{extension-id}", ::updateExtensionById)
    }
    private fun getAllExtensions(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { extensionHandler.getAllExtensions().collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ExtensionDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }

    private fun searchExtensions(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Query::class.java) }
            .flatMap { extensionHandler.searchExtensions(it) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(Flux.fromIterable(it.data), ExtensionDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }
    private fun getExtensionById(request: ServerRequest): Mono<ServerResponse> {
        val extensionId = request.pathVariable("extension-id")
        return authenticationHandler.principal(request)
            .flatMap { extensionHandler.getExtensionById(extensionId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ExtensionDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }

    private fun updateExtensionById(request: ServerRequest): Mono<ServerResponse> {
        val extensionId = request.pathVariable("extension-id")
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(ExtensionDTO::class.java) }
            .flatMap { extensionHandler.updateExtensionById(it.apply { id = extensionId }) }
            .flatMap {
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(it), ExtensionDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(AdminAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ExtensionNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }
}

