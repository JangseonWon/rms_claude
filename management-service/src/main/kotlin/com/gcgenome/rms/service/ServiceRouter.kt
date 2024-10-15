package com.gcgenome.rms.service

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.ServiceDTO
import com.gcgenome.rms.data.ServiceExtensionDTO
import com.gcgenome.rms.data.ServiceSampleTypeDTO
import com.gcgenome.rms.exception.AdminAuthenticationException
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ServiceNotFoundException
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
class ServiceRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val serviceHandler: ServiceHandler
) {
    @Bean("ServicesRouter")
    fun route() = router {
        POST("/w-api/management-service/services", ::findServices)
        GET("/w-api/management-service/services/{service_id}", ::findService)
        PATCH("/w-api/management-service/services/{service_id}", ::updateService)

        PUT("/w-api/management-service/services/{service_id}/sample-types/{sample_type_id}", ::saveServiceSampleType)
        DELETE("/w-api/management-service/services/{service_id}/sample-types/{sample_type_id}", ::deleteServiceSampleType)

        PUT("/w-api/management-service/services/{service_id}/extensions/{extension_id}", ::saveServiceExtension)
        DELETE("/w-api/management-service/services/{service_id}/extensions/{extension_id}", ::deleteServiceExtension)
    }
    private fun findServices(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .then(request.bodyToMono(Query::class.java).defaultIfEmpty(Query()))
            .flatMap { query -> serviceHandler.selectServices(query) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(Flux.fromIterable(it.data), ServiceDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it, ${it.cause}") }
    }

    private fun updateService(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(ServiceDTO::class.java) }
            .flatMap { serviceHandler.updateServiceById(it.apply { id = serviceId }) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(AdminAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : ${it.cause}") }
    }

    private fun findService(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.selectService(serviceId) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), ServiceDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : ${it.cause}") }
    }

    private fun saveServiceSampleType(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(ServiceSampleTypeDTO::class.java) }
            .flatMap { serviceHandler.insertServiceSampleType(it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ServiceSampleTypeDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : ${it.cause}") }
    }
    private fun deleteServiceSampleType(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        val sampleTypeId = request.pathVariable("sample_type_id")
        val serviceSampleType = ServiceSampleTypeDTO(sampleTypeId = sampleTypeId, serviceId = serviceId)
        return authenticationHandler.chkManager(request)
            .flatMap { serviceHandler.deleteServiceSampleType(serviceSampleType) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ServiceSampleTypeDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : ${it.cause}") }
    }

    private fun saveServiceExtension(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(ServiceExtensionDTO::class.java) }
            .flatMap { serviceHandler.insertServiceExtension(it) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), ServiceExtensionDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : ${it.cause}") }
    }

    private fun deleteServiceExtension(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service_id")
        val extensionId = request.pathVariable("extension_id")
        val serviceExtension = ServiceExtensionDTO(extensionId=extensionId, serviceId= serviceId)
        return authenticationHandler.chkManager(request)
            .flatMap { serviceHandler.deleteServiceExtension(serviceExtension) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : ${it.cause}") }
    }

}

