package com.gcgenome.rms.alis

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ManagerAuthenticationException
import com.gcgenome.rms.tables.pojos.Extension
import com.gcgenome.rms.tables.pojos.SampleType
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
        val query = Query(page = 1, size = 10)
        return authenticationHandler.chkManager(request)
            .then(alisHandler.updateServices(query))
            .flatMap {
                ServerResponse.ok()
                    .header("X-Total-Count", it.totalCount.toString())
                    .header("X-Total-Page", it.totalPage.toString())
                    .header("X-Page-Size", it.pageSize.toString())
                    .header("X-Current-Page", it.currentPage.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Flux.fromIterable(it.data), com.gcgenome.rms.data.ServiceDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }
    private fun sampleTypes(request: ServerRequest): Mono<ServerResponse> {
        val query = Query(page = 1, size = 10)
        return authenticationHandler.chkManager(request)
            .then(alisHandler.updateSampleTypes(query))
            .flatMap {
                ServerResponse.ok()
                    .header("X-Total-Count", it.totalCount.toString())
                    .header("X-Total-Page", it.totalPage.toString())
                    .header("X-Page-Size", it.pageSize.toString())
                    .header("X-Current-Page", it.currentPage.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Flux.fromIterable(it.data), SampleType::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue( "${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }
    private fun extensions(request: ServerRequest): Mono<ServerResponse> {
        val query = Query(page = 1, size = 10)
        return authenticationHandler.chkManager(request)
            .then(alisHandler.updateExtensions(query))
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
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }
}

