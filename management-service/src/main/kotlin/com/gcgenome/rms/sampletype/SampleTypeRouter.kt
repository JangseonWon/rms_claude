package com.gcgenome.rms.sampletype

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.SampleTypeDTO
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
class SampleTypeRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val sampleTypeHandler: SampleTypeHandler
) {
    @Bean("SampleTypeRouter")
    fun route() = router {
        POST("/w-api/management-service/sample-types", ::findSampleTypes)
    }

    private fun findSampleTypes(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(Query::class.java) }
            .flatMap { sampleTypeHandler.selectSampleTypes(it) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(Flux.fromIterable(it.data), SampleTypeDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it, ${it.cause}") }
    }
}

