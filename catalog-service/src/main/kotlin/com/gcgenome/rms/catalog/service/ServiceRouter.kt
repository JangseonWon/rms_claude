package com.gcgenome.rms.catalog.service

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
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
import javax.management.ServiceNotFoundException

@Configuration
class ServiceRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val serviceHandler: ServiceHandler
) {
    @Bean("ServiceRouter")
    fun route() = router {
        GET("/w-api/catalog-service/services", :: getServices)
        POST("/w-api/catalog-service/search", :: findUserWithServices)
        GET("/w-api/catalog-service/sample_types", :: getSampleTypeByServiceId)
        GET("/w-api/catalog-service/services/{serviceId}/extensions", ::serviceExtensions)
        GET("/w-api/catalog-service/services/{service-id}", ::findService)
    }

    private fun getServices(request: ServerRequest): Mono<ServerResponse> {
        val categoryId = UUID.fromString(request.queryParam("category_id").get())
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.getServices(it, categoryId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ServiceDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    private fun findUserWithServices(request: ServerRequest): Mono<ServerResponse> {
        return Mono.zip(authenticationHandler.principal(request),
            request.bodyToMono(Query::class.java).defaultIfEmpty(Query()))
            .flatMap { serviceHandler.selectUserWithServices(it.t1, it.t2).collectList() }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), Service::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it, ${it.cause}") }
    }

    private fun getSampleTypeByServiceId(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.queryParam("service_id").get()
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.getSampleTypes(serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), SampleTypeDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    private fun serviceExtensions(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("serviceId")
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.extensionByServiceId(serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ServiceExtensionDTO::class.java)}
            .onErrorResume (AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }

    private fun findService(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service-id")
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.selectService(serviceId).collectList() }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), ServiceDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}

