package com.gcgenome.rms.catalog.requestrelation

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.catalog.request.RequestRouter
import com.gcgenome.rms.data.RequestRelationDTO
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class RequestRelationRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val requestRelationHandler: RequestRelationHandler
) {
    @Bean("RequestRelationRouter")
    fun route() = router {
        GET("/w-api/catalog-service/request-relations", :: getRequestRelations)
    }
    private val logger: Logger = LoggerFactory.getLogger(RequestRelationRouter::class.java)

    private fun getRequestRelations(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { requestRelationHandler.getRequestGroups().collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), RequestRelationDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
}

