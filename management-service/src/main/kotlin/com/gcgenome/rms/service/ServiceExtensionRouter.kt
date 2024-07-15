package com.gcgenome.rms.service

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.tables.pojos.ServiceExtension
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class ServiceExtensionRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val managerAuthenticationHandler: ManagerAuthenticationHandler,
    private val serviceHandler: ServiceExtensionHandler
) {
    @Bean("ServiceExtensionRouter")
    fun route() = router {
        POST("/w-api/management-service/services/{service_id}/extensions", ::selectServiceExtensions)
        POST("/w-api/management-service/extensions", ::findExtensions)
        POST("/w-api/management-service/services/{service_id}/extensions/{extension_id}", ::insertExtension)
        DELETE("/w-api/management-service/services/{service_id}/extensions/{extension_id}", ::deleteExtension)
    }

    private fun selectServiceExtensions(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.principal(request)
            .flatMap { managerAuthenticationHandler.chkAdmin(it) }
            .flatMap { request.bodyToMono(Query.Companion.Filter::class.java) }
            .flatMap { serviceHandler.selectServiceExtensions(it, serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun findExtensions(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { managerAuthenticationHandler.chkAdmin(it) }
            .flatMap { request.bodyToMono(Query.Companion.Filter::class.java) }
            .flatMap { serviceHandler.selectExtensionAll(it).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun insertExtension(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { managerAuthenticationHandler.chkAdmin(it) }
            .flatMap { request.bodyToMono(ServiceExtension::class.java) }
            .flatMap { serviceHandler.insertServiceExtension(it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun deleteExtension(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        val extensionId = request.pathVariable("extension_id")
        return authenticationHandler.principal(request)
            .flatMap { managerAuthenticationHandler.chkAdmin(it) }
            .flatMap { serviceHandler.deleteServiceExtension(serviceId, extensionId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }
}

