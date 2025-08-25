package com.gcgenome.rms.labs.organization

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
class OrganizationRouter (
    private val organizationHandler: OrganizationHandler
){
    @Bean
    fun organizationRoute() = router {
        accept(MediaType.parseMediaType("application/vnd.gcgenome.v1+json")).nest {
            contentType(MediaType.APPLICATION_JSON).nest {
                PUT("/labs/organizations/{organization-serial}", ::saveOrganization)
                POST("/labs/organizations/search", ::searchOrganizations)
            }
            GET("/labs/organizations/{organization-serial}", :: getOrganizationBySerial)
            DELETE("/labs/organizations/{organization-serial}", :: deleteOrganization)
        }
    }

    private val logger: Logger = LoggerFactory.getLogger(OrganizationRouter::class.java)

    private fun saveOrganization(request: ServerRequest): Mono<ServerResponse> {
        val organizationSerial = request.pathVariable("organization-serial")
        val principalMono = request.principal().cast(CustomAuthenticationToken::class.java)
        val bodyMono = request.bodyToMono(OrganizationDTO::class.java)

        return Mono.zip(principalMono, bodyMono)
            .flatMap { tuple -> organizationHandler.saveOrganization(tuple.t1.user.id, tuple.t2, organizationSerial) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), OrganizationDTO::class.java) }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }

    private fun getOrganizationBySerial(request: ServerRequest): Mono<ServerResponse> {
        val organizationSerial = request.pathVariable("organization-serial")

        return request.principal().cast(CustomAuthenticationToken::class.java)
            .flatMap { token -> organizationHandler.getOrganizationBySerial(token.user.id, organizationSerial) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), OrganizationDTO::class.java) }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }

    private fun searchOrganizations(request: ServerRequest): Mono<ServerResponse> {
        val principalMono = request.principal().cast(CustomAuthenticationToken::class.java)
        val bodyMono = request.bodyToMono(OrganizationDTO::class.java).defaultIfEmpty(OrganizationDTO())

        return Mono.zip(principalMono, bodyMono)
            .flatMap { tuple -> organizationHandler.searchOrganization(tuple.t1.user.id, tuple.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), OrganizationDTO::class.java) }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }

    private fun deleteOrganization(request: ServerRequest): Mono<ServerResponse> {
        val organizationSerial = request.pathVariable("organization-serial")

        return request.principal().cast(CustomAuthenticationToken::class.java)
            .flatMap { token -> organizationHandler.deleteOrganizationBySerial(token.user.id, organizationSerial) }
            .flatMap { ServerResponse.ok().build()}
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }

}
