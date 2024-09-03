package com.gcgenome.rms.service

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.AdminAuthenticationException
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.tables.pojos.Service
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class ServiceRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val serviceHandler: ServiceHandler
) {
    @Bean("ServicesRouter")
    fun route() = router {
        POST("/w-api/management-service/services/users/{user_id}", ::selectUserServices)
        GET("/w-api/management-service/services/{service_id}", ::selectService)
        POST("/w-api/management-service/service_category", ::selectServiceCategory)
        POST("/w-api/management-service/services", ::selectServices)
        PATCH("/w-api/management-service/services/{service_id}", ::updateServiceByCategoryId)
    }

    private fun updateServiceByCategoryId(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(Service::class.java) }
            .flatMap { serviceHandler.updateServiceById(it.apply { id = serviceId }) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(AdminAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun selectUserServices(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user_id")
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Query.Companion.Filter::class.java) }
            .flatMap { serviceHandler.selectServiceByUserId(userId, it).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun selectService(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.selectService(serviceId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun selectServiceCategory(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .then(request.bodyToMono(Query::class.java))
            .flatMap { query -> serviceHandler.selectServiceCategory(query.copy(page=query.page - 1)) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Page", it.totalPage.toString())
                .bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun selectServices(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Query.Companion.Filter::class.java) }
            .flatMap { serviceHandler.selectServiceByNameOrId(it).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }
}

