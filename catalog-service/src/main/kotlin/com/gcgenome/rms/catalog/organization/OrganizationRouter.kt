package com.gcgenome.rms.catalog.organization

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import com.gcgenome.rms.tables.pojos.Organization
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
        GET("/w-api/catalog-service/organizations", ::getOrganizations)
    }
    private fun getOrganizations(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { organizationHandler.getOrganizations(it.user.id!!).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Organization::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}

