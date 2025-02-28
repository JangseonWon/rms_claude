package com.gcgenome.rms.alis

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.categories.CategoriesRouter
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.SampleTypeDTO
import com.gcgenome.rms.data.ServiceDTO
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ManagerAuthenticationException
import com.gcgenome.rms.tables.pojos.Extension
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
class AlisRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val alisHandler: AlisHandler
) {
    @Bean("AlisRouter")
    fun route() = router {
        PUT("/w-api/management-service/alis/users", :: users)
        PUT("/w-api/management-service/alis/services", :: services)
        PUT("/w-api/management-service/alis/sample-types", :: sampleTypes)
        PUT("/w-api/management-service/alis/extensions", :: extensions)
    }
    private val logger: Logger = LoggerFactory.getLogger(AlisRouter::class.java)
    private fun users(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request).zipWith(request.bodyToMono(Query::class.java))
            .flatMap { alisHandler.updateUsers(it.t1.user.id!!, it.t2) }
            .flatMap {
                ServerResponse.ok()
                    .header("X-Total-Count", it.totalCount.toString())
                    .header("X-Total-Page", it.totalPage.toString())
                    .header("X-Page-Size", it.pageSize.toString())
                    .header("X-Current-Page", it.currentPage.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(it.data), UserDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }

    private fun services(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .then(request.bodyToMono(Query::class.java))
            .flatMap { alisHandler.updateServices(it) }
            .flatMap {
                ServerResponse.ok()
                    .header("X-Total-Count", it.totalCount.toString())
                    .header("X-Total-Page", it.totalPage.toString())
                    .header("X-Page-Size", it.pageSize.toString())
                    .header("X-Current-Page", it.currentPage.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Flux.fromIterable(it.data), ServiceDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
    private fun sampleTypes(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .then(request.bodyToMono(Query::class.java))
            .flatMap { alisHandler.updateSampleTypes(it) }
            .flatMap {
                ServerResponse.ok()
                    .header("X-Total-Count", it.totalCount.toString())
                    .header("X-Total-Page", it.totalPage.toString())
                    .header("X-Page-Size", it.pageSize.toString())
                    .header("X-Current-Page", it.currentPage.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Flux.fromIterable(it.data), SampleTypeDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
    private fun extensions(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .then(request.bodyToMono(Query::class.java))
            .flatMap { alisHandler.updateExtensions(it) }
            .flatMap {
                ServerResponse.ok()
                    .header("X-Total-Count", it.totalCount.toString())
                    .header("X-Total-Page", it.totalPage.toString())
                    .header("X-Page-Size", it.pageSize.toString())
                    .header("X-Current-Page", it.currentPage.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Flux.fromIterable(it.data), Extension::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
}

