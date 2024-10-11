package com.gcgenome.rms.profile.organization

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.data.Query
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
        POST("/w-api/profile-service/organizations", ::selectUserWithOrganization)
        PUT("/w-api/profile-service/users/{user-id}/organizations/{organization-id}", ::insertOrganization)
        PATCH("/w-api/profile-service/users/{user-id}/organizations/{organization-id}", ::updateOrganization)
    }
    private fun selectUserWithOrganization(request: ServerRequest): Mono<ServerResponse> {
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(Query::class.java))
            .flatMap { organizationHandler.selectUserWithOrganizations(it.t1, it.t2) }
            .flatMap { ServerResponse.ok()
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it.data), OrganizationDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error: ${it.cause}") }
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

