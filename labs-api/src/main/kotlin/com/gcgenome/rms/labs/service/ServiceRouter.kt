package com.gcgenome.rms.labs.service

import com.gcgenome.rms.config.CustomAuthenticationToken
import com.gcgenome.rms.data.*
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
class ServiceRouter (
    private val serviceHandler: ServiceHandler
){
    @Bean
    fun serviceRoute() = router {
        accept(MediaType.parseMediaType("application/vnd.gcgenome.v1+json")).nest {
            contentType(MediaType.APPLICATION_JSON).nest {
                POST("/labs/services/search", ::searchServices)
            }
        }
    }
    private val logger: Logger = LoggerFactory.getLogger(ServiceRouter::class.java)


    private fun searchServices(request: ServerRequest): Mono<ServerResponse> {
        val principalMono = request.principal().cast(CustomAuthenticationToken::class.java)
        val searchService = request.bodyToMono(ServiceDTO::class.java).defaultIfEmpty(ServiceDTO())

        return Mono.zip(principalMono, searchService)
            .flatMap { tuple -> serviceHandler.searchServices(tuple.t1.user.id, tuple.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ServiceDTO::class.java) }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
}
