package com.gcgenome.rms.profile.organization

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
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
    private val authenticationHandler: AuthenticationHandler,
    private val organizationHandler: OrganizationHandler
) {
    @Bean("OrganizationRouter")
    fun route() = router {
        PUT("/w-api/profile-service/users/{user-id}/organizations/{organization-id}", ::insertOrganization)
        PATCH("/w-api/profile-service/users/{user-id}/organizations/{organization-id}", ::updateOrganization)
    }

    private fun insertOrganization(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user-id")
        val organizationId = request.pathVariable("organization-id")
        return authenticationHandler.chkUser(request, userId)
            .flatMap { request.bodyToMono(OrganizationDTO::class.java) }
            .flatMap { organizationHandler.insertOrganization(it.apply { this.userId = userId; this.id = organizationId}) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : ${it.cause}") }
    }

    private fun updateOrganization(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user-id")
        val organizationId = request.pathVariable("organization-id")
        return authenticationHandler.chkUser(request, userId)
            .flatMap { request.bodyToMono(OrganizationDTO::class.java) }
            .flatMap { organizationHandler.updateOrganization(it.apply { id = organizationId; this.userId = userId }) }
            .flatMap { ServerResponse.status(HttpStatus.OK).build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : ${it.cause}") }
    }
}

