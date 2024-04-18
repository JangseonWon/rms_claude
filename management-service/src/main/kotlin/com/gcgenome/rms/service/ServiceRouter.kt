package com.gcgenome.rms.service

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.CategoryNotFoundException
import com.gcgenome.rms.exception.ManagerAuthenticationException
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.tables.pojos.Category
import com.gcgenome.rms.tables.pojos.Service
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class ServiceRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val categoriesHandler: ServiceHandler
) {
    @Bean("ServicesRouter")
    fun route() = router {
        POST("/w-api/management-service/services", ::selectServices)
        POST("/w-api/management-service/services/{service_id}/extensions", ::selectServiceExtensions)
        POST("/w-api/management-service/services/{service_id}/sample-types", ::selectSampleTypes)
    }

    private fun selectServices(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Query.Companion.Filter::class.java) }
            .flatMap { categoriesHandler.selectServiceByNameOrId(it).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun selectServiceExtensions(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Query.Companion.Filter::class.java) }
            .flatMap { categoriesHandler.selectServiceExtensions(it, serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun selectSampleTypes(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Query.Companion.Filter::class.java) }
            .flatMap { categoriesHandler.selectSampleTypes(it, serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }
}

