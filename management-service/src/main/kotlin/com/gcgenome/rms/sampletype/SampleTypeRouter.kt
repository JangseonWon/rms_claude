package com.gcgenome.rms.sampletype

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.SampleTypeDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.extension.ExtensionRouter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
class SampleTypeRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val sampleTypeHandler: SampleTypeHandler
) {
    @Bean("SampleTypeRouter")
    fun route() = router {
        GET("/w-api/management-service/sample-types/{sample-type-id}", ::getSampleTypeById)
        GET("/w-api/management-service/sample-types", ::getAllSampleTypes)
        POST("/w-api/management-service/sample-types/search", ::searchSampleTypes)
        PATCH("/w-api/management-service/sample-types/{sample-type-id}", ::updateSampleTypeById)
    }
    private val logger: Logger = LoggerFactory.getLogger(SampleTypeRouter::class.java)

    private fun getSampleTypeById(request: ServerRequest): Mono<ServerResponse> {
        val sampleTypeId = request.pathVariable("sample-type-id")
        return authenticationHandler.chkManager(request)
            .flatMap { sampleTypeHandler.getSampleTypeById(sampleTypeId) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), SampleTypeDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
    private fun getAllSampleTypes(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .flatMap { sampleTypeHandler.getAllSampleTypes().collectList() }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), SampleTypeDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }


    private fun searchSampleTypes(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(Query::class.java) }
            .flatMap { sampleTypeHandler.searchSampleTypes(it) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(Flux.fromIterable(it.data), SampleTypeDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
    private fun updateSampleTypeById(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(SampleTypeDTO::class.java) }
            .flatMap { sampleType -> sampleTypeHandler.updateSampleType(sampleType) }
            .then(ServerResponse.ok().build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
}

