package com.gcgenome.rms.organization

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.data.Query
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
) {
    @Bean
    fun route() = router {
        PUT("/w-api/organization-service/organizations", ::insertOrganization)
        POST("/w-api/organization-service/organizations", ::selectOrganizations)
    }

    fun insertOrganization(request: ServerRequest) : Mono<ServerResponse> {
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Organization::class.java))
            .flatMap {
                organizationHandler.insertOrganization(it.t1.principal, it.t2) }
            .flatMap { organization ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(organization)
            }.onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }

    fun selectOrganizations(request: ServerRequest): Mono<ServerResponse> {
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Query::class.java))
            .flatMap {
                organizationHandler.selectOrganizations(it.t1.principal, it.t2.copy(page= it.t2.page - 1)) }
            .flatMap { organizations ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(organizations)
            }.onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}
